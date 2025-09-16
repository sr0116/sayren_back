package com.imchobo.sayren_back.domain.payment.service;


import com.imchobo.sayren_back.domain.common.config.ApiResponse;
import com.imchobo.sayren_back.domain.exentity.Order;
import com.imchobo.sayren_back.domain.exentity.OrderItem;
import com.imchobo.sayren_back.domain.exentity.OrderPlan;
import com.imchobo.sayren_back.domain.exentity.OrderPlanRepository;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.portone.client.PortOnePaymentClient;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.portone.dto.cancel.CancelRequest;
import com.imchobo.sayren_back.domain.payment.portone.dto.cancel.CancelResponse;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentInfoResponse;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeMapper;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import com.imchobo.sayren_back.domain.subscribe_payment.service.SubscribePaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  // PortOne api 호출 및 연동
  private final PortOnePaymentClient portOnePaymentClient;

  // transaction 필요
  private final SubscribeService subscribeService;
  private final SubscribeMapper subscribeMapper;
  private final SubscribePaymentService subscribePaymentService;

  // 임시 레포지토리
  private final OrderPlanRepository orderPlanRepository;


  @Transactional
  @Override
  public ApiResponse<PaymentResponseDTO> prepare(PaymentRequestDTO dto) {
    Payment payment = paymentMapper.toEntity(dto);

    // 주문 연결
    Order order = payment.getOrder();
    if (order == null) {
      order = Order.builder()
              .id(dto.getOrderId())
              .build();
      payment.setOrder(order);
    }

    // merchantUid 생성
    String merchantUid = "pay_" + UUID.randomUUID().toString().replace("-", "");
    payment.setMerchantUid(merchantUid);

    paymentRepository.save(payment);

    return ApiResponse.ok(paymentMapper.toResponseDTO(payment));
  }

  @Transactional
  @Override
  public ApiResponse<PaymentResponseDTO> complete(Long paymentId, String impUid) {
    // Payment + Order + OrderItems + Plan까지 조인해서 가져오기
    Payment payment = paymentRepository.findWithOrderAndPlan(paymentId)
            .orElseThrow(() -> new RuntimeException("결제에 연결된 주문/플랜 정보를 찾을 수 없습니다."));

    // PortOne 결제 정보 조회
    PaymentInfoResponse paymentInfo = portOnePaymentClient.getPaymentInfo(impUid);
    log.info("PortOne 결제 정보: {}", paymentInfo);

    // 상태 검증
    PaymentStatus mappedStatus = PaymentStatus.fromPortOneStatus(paymentInfo.getStatus());
    if (mappedStatus != PaymentStatus.PAID) {
      payment.setPayStatus(mappedStatus);
      paymentRepository.save(payment);
      throw new RuntimeException("결제 미완료 상태: " + paymentInfo.getStatus());
    }

    // 금액 검증
    if (!paymentInfo.getAmount().equals(payment.getAmount())) {
      log.warn("결제 금액 불일치: 요청 금액={}, PortOne 금액={}", payment.getAmount(), paymentInfo.getAmount());
      return ApiResponse.fail("결제 금액이 일치하지 않습니다.");
    }

    // 결제 완료 처리
    payment.setImpUid(impUid);
    payment.setPayStatus(PaymentStatus.PAID);
    paymentRepository.save(payment);

    // 주문 및 플랜 확인
    Order order = payment.getOrder();
    OrderItem orderItem = order.getOrderItems().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("주문에 연결된 아이템이 없습니다."));
    OrderPlan plan = orderItem.getPlan();
    if (plan == null) {
      throw new RuntimeException("결제에 연결된 플랜 정보가 없습니다.");
    }

    // 플랜 타입별 분기
    if ("PURCHASE".equalsIgnoreCase(plan.getType())) {
      log.info("일반 결제 처리 완료: {}", plan);
    } else if ("RENTAL".equalsIgnoreCase(plan.getType())) {
      log.info("구독 결제 처리 완료: {}", plan);
      SubscribeRequestDTO subscribeRequest = subscribeMapper.toRequestDTO(orderItem, order, plan);
      SubscribeResponseDTO subscribe = subscribeService.create(subscribeRequest);
      subscribePaymentService.generateRounds(subscribe, payment);
    }

    return ApiResponse.ok(paymentMapper.toResponseDTO(payment));
  }



  // 환불 처리 (아직 반영 안됨)
  @Override
  @Transactional
  public ApiResponse<Void> refund(Long paymentId, Long amount, String reason) {

    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("결제 아이디를 찾을 수 없습니다."));
// 환불 요청
    CancelRequest cancelRequest = new CancelRequest(
            payment.getImpUid(),
            payment.getMerchantUid(),
            reason,
            amount
    );
    // api 호출
    CancelResponse cancelResponse = portOnePaymentClient.cancelPayment(cancelRequest);
// DB 저장
    payment.setPayStatus(PaymentStatus.REFUNDED);
    paymentRepository.save(payment);

    return ApiResponse.ok(null);
  }

//  조회 리스트 (JPA)


  @Override
  public ApiResponse<List<PaymentResponseDTO>> getAll() {
    List<PaymentResponseDTO> list = paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "regdate"))
            .stream()
            .map(paymentMapper::toResponseDTO)
            .toList();
    return ApiResponse.ok(list);
  }
}
