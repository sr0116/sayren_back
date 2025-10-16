package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.component.PaymentStatusChanger;
import com.imchobo.sayren_back.domain.payment.component.recorder.PaymentHistoryRecorder;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.en.PaymentType;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentAlreadyExistsException;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.exception.PaymentVerificationFailedException;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentHistoryMapper;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.portone.client.PortOnePaymentClient;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentInfoResponse;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRepository;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundRequestService;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundService;
import com.imchobo.sayren_back.domain.payment.repository.PaymentHistoryRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round.SubscribeRoundNotFoundException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

  // PortOne api 호출 및 연동
  private final PortOnePaymentClient portOnePaymentClient;
  // 상태 변경 컴포넌트 이벤트 처리
  private final PaymentStatusChanger paymentStatusChanger;
  private final PaymentHistoryRecorder paymentHistoryRecorder;
  private final RefundRepository refundRepository;
  private final MemberRepository memberRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final DeliveryItemRepository deliveryItemRepository;
  private final PaymentHistoryRepository paymentHistoryRepository;

  // 결제 준비
  // 연계 - 구독 테이블, 구독 회차 테이블 (구독 결제시)
  @Transactional
  @Override
  public PaymentResponseDTO prepare(PaymentRequestDTO dto) {
    // 현재 로그인 한 멤버 정보 조회
    Member currentMember = SecurityUtil.getMemberEntity();
    if (currentMember.getName() == null || currentMember.getEmail() == null) {
      currentMember = memberRepository.findById(currentMember.getId())
              .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

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
//    payment.setPaymentType(PaymentType.CARD); // 포트원에서 세팅


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

  // 결제 삭제
  @Transactional
  @Override
  public void deletePayment(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));

    OrderItem orderItem = payment.getOrderItem();

    // 1. 배송 중, 회수 중이면 삭제 불가
    List<DeliveryItem> deliveryItems = deliveryItemRepository.findByOrderItem(orderItem);
    if (!deliveryItems.isEmpty()) {
      Delivery delivery = deliveryItems.get(0).getDelivery();
      if (delivery != null && (
              delivery.getStatus() == DeliveryStatus.SHIPPING ||
                      delivery.getStatus() == DeliveryStatus.IN_RETURNING ||
                      delivery.getStatus() == DeliveryStatus.RETURN_READY
      )) {
        throw new PaymentVerificationFailedException("배송 또는 회수 중인 결제는 삭제할 수 없습니다.");
      }
    }

    // 2. 환불 요청 진행 중이면 삭제 불가
    boolean refundInProgress = refundRequestRepository.existsByOrderItemAndStatusIn(
            orderItem,
            List.of(
                    RefundRequestStatus.PENDING,
                    RefundRequestStatus.APPROVED_WAITING_RETURN
            )
    );
    if (refundInProgress) {
      throw new PaymentVerificationFailedException("환불 처리 중인 결제는 삭제할 수 없습니다.");
    }

    // 3. 관련 구독 삭제 (진행 중인 상태 제외)
    Subscribe subscribe = subscribeRepository.findByOrderItem(orderItem).orElse(null);
    if (subscribe != null) {
      if (List.of(SubscribeStatus.PREPARING, SubscribeStatus.ACTIVE, SubscribeStatus.OVERDUE)
              .contains(subscribe.getStatus())) {
        throw new PaymentVerificationFailedException("진행 중인 구독과 연결된 결제는 삭제할 수 없습니다.");
      }

      // 회차 먼저 삭제
      List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
      if (!rounds.isEmpty()) {
        subscribeRoundRepository.deleteAll(rounds);
        log.info("[DELETE] 구독 회차 {}건 삭제 완료 (subscribeId={})", rounds.size(), subscribe.getId());
      }

      subscribeRepository.delete(subscribe);
      log.info("[DELETE] 구독 삭제 완료 (subscribeId={})", subscribe.getId());
    }

    // 4. 환불 내역 선삭제
    refundRepository.deleteAllByPayment(payment);
    log.info("[DELETE] 환불 내역 삭제 완료 (paymentId={})", paymentId);

    // 5. 결제 이력 선삭제 (FK 제약 방지)
    paymentHistoryRepository.deleteAllByPayment(payment);
    log.info("[DELETE] 결제 이력 삭제 완료 (paymentId={})", paymentId);

    // 6. 결제 삭제
    paymentRepository.delete(payment);
    log.info("[DELETE] 결제 삭제 완료 (paymentId={})", paymentId);
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
    log.info("DB 결제 금액={}, PortOne 결제 금액={}", payment.getAmount(), paymentInfo.getAmount());

    // 포트원 매핑
    PaymentTransition transition = PaymentTransition.fromPortOne(paymentInfo, payment.getAmount());

//  상태 반영
    payment.setImpUid(impUid);
    payment.setReceipt(paymentInfo.getReceiptUrl());
    if (paymentInfo.getPaymentType() != null) {
      payment.setPaymentType(paymentInfo.getPaymentType());
    } else {
      // 기본값 세팅
      payment.setPaymentType(PaymentType.CARD);
    }

    paymentStatusChanger.changePayment(payment, transition, payment.getOrderItem().getId(), ActorType.SYSTEM);

    // 회차 결제시 회차 상태 갱신
    if (payment.getSubscribeRound() != null) {
      SubscribeRound round = payment.getSubscribeRound();
      switch (transition) {
        case COMPLETE -> {
          round.setPayStatus(PaymentStatus.PAID);
          round.setPaidDate(LocalDateTime.now());
          log.info("회차 결제 성공: roundId={}, paidDate={}", round.getId(), round.getPaidDate());
        }
        case FAIL_USER, FAIL_PAYMENT, FAIL_SYSTEM, FAIL_TIMEOUT -> {
          round.setPayStatus(PaymentStatus.FAILED);
          log.warn("회차 결제 실패: roundId={}, transition={}", round.getId(), transition);
        }
        case REFUND, PARTIAL_REFUND -> {
          round.setPayStatus(PaymentStatus.REFUNDED);
          log.info("회차 환불 처리: roundId={}, transition={}", round.getId(), transition);

        }
      }
    }
    return paymentMapper.toResponseDTO(payment);
  }

  // 회차 결제 준비용
  @Override
  @Transactional
  public PaymentResponseDTO prepareForRound(Long subscribeRoundId) {
    SubscribeRound round = subscribeRoundRepository.findById(subscribeRoundId)
            .orElseThrow(() -> new RuntimeException("회차를 찾을 수 없습니다."));
    return prepareForRound(round);
  }

  // 회차 결제 준비용
  @Override
  @Transactional
  public PaymentResponseDTO prepareForRound(SubscribeRound round) {
    Member currentMember = SecurityUtil.getMemberEntity();
    if (currentMember.getName() == null || currentMember.getEmail() == null) {
      currentMember = memberRepository.findById(currentMember.getId())
              .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }
    OrderItem orderItem = round.getSubscribe().getOrderItem();

    String merchantUid = "pay_" + UUID.randomUUID().toString().replace("-", "");
    if (paymentRepository.findByMerchantUid(merchantUid).isPresent()) {
      throw new PaymentAlreadyExistsException(merchantUid);
    }

    Payment payment = paymentMapper.toEntityFromRound(round);
    payment.setMember(currentMember);
    payment.setOrderItem(orderItem);
    payment.setMerchantUid(merchantUid);
    payment.setPaymentType(PaymentType.CARD);
    payment.setAmount(round.getAmount());

    Payment savedPayment = paymentRepository.saveAndFlush(payment);
    paymentHistoryRecorder.recordInitPayment(savedPayment);

    return paymentMapper.toResponseDTO(savedPayment);
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
  @Transactional
  public PaymentResponseDTO getOne(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));

    Member currentMember = SecurityUtil.getMemberEntity();
    if (!Objects.equals(payment.getMember().getId(), currentMember.getId())) {
      throw new RuntimeException("본인 결제 내역만 조회할 수 있습니다.");
    }

    PaymentResponseDTO dto = paymentMapper.toResponseDTO(payment);

    // Refund 통해 RefundRequest 가져오기
    Refund refund = refundRepository.findFirstByPaymentOrderByRegDateDesc(payment).orElse(null);

    if (refund != null) {
      dto.setRefundStatus(refund.getRefundRequest().getStatus());
    } else {
      dto.setRefundStatus(null);
    }

    return dto;
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentResponseDTO> getAll() {
    Member currentMember = SecurityUtil.getMemberEntity();
    List<Payment> payments = paymentRepository.findByMemberOrderByRegDateDesc(currentMember);

    return payments.stream().map(payment -> {
      PaymentResponseDTO dto = paymentMapper.toResponseDTO(payment);

      Refund refund = refundRepository.findFirstByPaymentOrderByRegDateDesc(payment).orElse(null);
      dto.setRefundStatus(refund != null ? refund.getRefundRequest().getStatus() : null);

      return dto;
    }).collect(Collectors.toList());
  }

  // 관리자용
  @Transactional(readOnly = true)
  @Override
  public List<PaymentResponseDTO> getAllForAdmin() {
    List<Payment> payments = paymentRepository.findAllWithMemberAndOrder();
    return payments.stream()
            .map(paymentMapper::toResponseDTO)
            .toList();
  }
}
