package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.component.PaymentStatusChanger;
import com.imchobo.sayren_back.domain.payment.component.recorder.PaymentHistoryRecorder;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentAlreadyExistsException;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentHistoryMapper;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.portone.client.PortOnePaymentClient;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentInfoResponse;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeMapper;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

  // DB
  private final PaymentRepository paymentRepository;
  private final OrderItemRepository orderItemRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final SubscribeRepository subscribeRepository;

  // 서비스
  private final SubscribeService subscribeService;

  // 매퍼
  private final SubscribeMapper subscribeMapper;
  private final PaymentMapper paymentMapper;
  private final PaymentHistoryMapper paymentHistoryMapper;

  // PortOne api 호출 및 연동
  private final PortOnePaymentClient portOnePaymentClient;
  // 상태 변경 컴포넌트 이벤트 처리
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final PaymentStatusChanger paymentStatusChanger;
  private final PaymentHistoryRecorder paymentHistoryRecorder;


  // 결제 준비
  // 연계 - 구독 테이블, 구독 회차 테이블 (구독 결제시)
  @Transactional
  @Override
  public PaymentResponseDTO prepare(PaymentRequestDTO dto) {
    // 현재 로그인 한 멤버 정보 조회
    Member currentMember = SecurityUtil.getMemberEntity();

    // 주문 아이템 조회(예외 처리 나중에 추가)
    OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
            .orElseThrow(() -> new RuntimeException("주문 아이템을 찾을 수 없습니다."));
    // 구독 인지 일반인지 조회
    OrderPlanType planType = orderItem.getOrderPlan().getType();

    //  구독 결제(Rental)일 경우 → 구독 + 회차 생성
    Subscribe subscribe = createSubscribe(planType, orderItem);

    // portOne 고유 식별자 (merchantUid) 생성
    String merchantUid = "pay_" + UUID.randomUUID().toString().replace("-", "");
    // metchantUid 중복 불가 예외 처리, null 값은 이미 처리해둠
    if (paymentRepository.findByMerchantUid(merchantUid).isPresent()) {
      throw new PaymentAlreadyExistsException(merchantUid);
    }

    // DTO -> 엔티티 변환 결제 테이블
    Payment payment = paymentMapper.toEntity(dto);
    payment.setMember(currentMember);
    payment.setMerchantUid(merchantUid);
    payment.setOrderItem(orderItem);

    // 1회차 결제 고정
    if (planType == OrderPlanType.RENTAL) {
      SubscribeRound firstRound = subscribeRoundRepository
              .findBySubscribeIdAndRoundNo(subscribe.getId(), 1)
              .orElseThrow(() -> new RuntimeException("1회차 회차를 찾을 수 없습니다."));
      // 상품 가격 + 1회차(렌탈료+ 보증금) -> payment.getAmount()+firstRound.getAmount() 나중에 금액 조회시 필요
      payment.setSubscribeRound(firstRound);
      payment.setAmount(firstRound.getAmount());
    } else {
      // 일반 결제는 OrderItem 금액 사용
      payment.setAmount(orderItem.getProductPriceSnapshot());
    }
    // DB 저장
    Payment savedPayment = paymentRepository.saveAndFlush(payment);

    // 최초 결제 이력 기록 (동기, 원자성)
    paymentHistoryRecorder.recordInitPayment(savedPayment);

    return paymentMapper.toResponseDTO(savedPayment);
  }

  // 구독 생성
  private Subscribe createSubscribe(OrderPlanType planType, OrderItem orderItem) {
    if (planType == OrderPlanType.RENTAL) {
      // 무조건 새 구독 생성
      SubscribeRequestDTO dto =
              subscribeMapper.toRequestDTO(orderItem, orderItem.getOrder(), orderItem.getOrderPlan());
      return subscribeService.createSubscribe(dto, orderItem);
    }
    return null;
  }

  // 결제 응답
  @Override
  @Transactional
  public PaymentResponseDTO complete(Long paymentId, String impUid) {
    // payment 조회(OrderItem, paln join)
    Payment payment = paymentRepository.findWithOrderAndPlan(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    // 결제 정보 조회
    PaymentInfoResponse paymentInfo = portOnePaymentClient.getPaymentInfo(impUid);
    log.info("PortOne 결제 정보: {}", paymentInfo);

    // 포트원 매핑
    PaymentTransition transition = PaymentTransition.fromPortOne(paymentInfo, payment.getAmount());

//  상태 반영
    payment.setImpUid(impUid);

    paymentStatusChanger.changePayment(payment, transition, payment.getOrderItem().getId(), ActorType.SYSTEM);

//// 성공일 때만 구독/회차 처리 (이벤트에서 처리하는 방향 나중에 없애기)
//    if (payment.getSubscribeRound() != null) {
//      Subscribe subscribe = payment.getSubscribeRound().getSubscribe();
//      SubscribeRound round = payment.getSubscribeRound();
//      switch (transition) {
//        case COMPLETE -> {
//          subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.PREPARE, ActorType.SYSTEM);
//          subscribeStatusChanger.changeSubscribeRound(round, SubscribeRoundTransition.PAY_SUCCESS);
//        }
//        case FAIL_USER, FAIL_PAYMENT, FAIL_SYSTEM, FAIL_TIMEOUT -> {
//          subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.FAIL_PAYMENT, ActorType.SYSTEM);
//          subscribeStatusChanger.changeSubscribeRound(round, SubscribeRoundTransition.PAY_FAIL);
//        }
//        case REFUND, PARTIAL_REFUND -> {
//          subscribeStatusChanger.changeSubscribe(subscribe, SubscribeTransition.RETURNED_AND_CANCELED, ActorType.SYSTEM);
//          subscribeStatusChanger.changeSubscribeRound(round, SubscribeRoundTransition.CANCEL);
//        }
//      }
//    }
    return paymentMapper.toResponseDTO(payment);
  }
  @Override
  @Transactional
  public void refund(Long paymentId, Long amount, String reason) {
    // 나중에 payment 에서 refund 서비스 위임해서 히스토리만 기록하거나
    // reason code만 따로 변경하게 상태값 변경
    // 구독 일반 결제 구분해서
//    refundService.requestRefund(paymentId, amount, reason);

  }

  // 사용자 전용 전체 결제 내역(요약)
  @Override
  @Transactional(readOnly = true)
  public List<PaymentSummaryDTO> getSummaries() {
    Member currentMember = SecurityUtil.getMemberEntity();

    List<Payment> payments = paymentRepository.findByMemberOrderByRegDateDesc(currentMember);
    log.info("결제 내역 조회, memberId={}, size={}", currentMember.getId(), payments.size());

    return paymentMapper.toSummaryDTOList(payments);
  }

  // 사용자 전용 전체 결제 내역
  @Override
  @Transactional(readOnly = true)
  public List<PaymentResponseDTO> getAll() {
    Member currentMember = SecurityUtil.getMemberEntity();
    // 현재 로그인한 사용자 기준 최근 결제 내역 조회
    List<Payment> payments = paymentRepository
            .findByMemberOrderByRegDateDesc(currentMember);

    return payments.stream()
            .map(paymentMapper::toResponseDTO)
            .toList();
  }
  // 관리자용
  @Transactional(readOnly = true)
  @Override
  public List<PaymentResponseDTO> getAllForAdmin() {
    List<Payment> payments = paymentRepository.findAllByOrderByIdDesc();

    return payments.stream()
            .map(paymentMapper::toResponseDTO)
            .toList();
  }
}
