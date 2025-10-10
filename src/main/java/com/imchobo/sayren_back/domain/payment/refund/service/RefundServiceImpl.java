package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.payment.calculator.PurchaseRefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RentalRefundCalculator;
import com.imchobo.sayren_back.domain.payment.component.PaymentStatusChanger;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.portone.client.PortOnePaymentClient;
import com.imchobo.sayren_back.domain.payment.portone.dto.cancel.CancelRequest;
import com.imchobo.sayren_back.domain.payment.portone.dto.cancel.CancelResponse;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundCompletedEvent;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.mapper.SubscribeMapper;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

  private final RefundRepository refundRepository;
  private final PaymentRepository paymentRepository;
  private final PurchaseRefundCalculator purchaseRefundCalculator;
  private final RentalRefundCalculator rentalRefundCalculator;
  private final SubscribeMapper subscribeMapper;
  private final SubscribeStatusChanger subscribeStatusChanger;
  private final SubscribeRepository subscribeRepository;
  private final PaymentStatusChanger paymentStatusChanger;
  private final PortOnePaymentClient portOnePaymentClient;
  private final ApplicationEventPublisher eventPublisher;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final DeliveryItemRepository deliveryItemRepository;


  //  환불 결제 처리
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void executeRefund(RefundRequest request, ReasonCode reasonCode) {
    List<Payment> payments = paymentRepository.findByOrderItem(request.getOrderItem());
    if (payments.isEmpty()) {
      throw new PaymentNotFoundException(request.getOrderItem().getId());
    }
    Payment payment = payments.get(payments.size() - 1); // 최근 결제

    // 자동 환불 조건 확인
    if (isAutoRefund(payment)) {
      log.info("자동 환불 조건 충족 -> 24시간 이내 또는 배송 준비상태, 전액 환불처리");
      processAutoRefund(payment, request);
      return; // 자동 환불 후 종료
    }

    // 결제 유형 분기 처리 (일반 결제)
    RefundCalculator calculator = getCalculator(payment);
    Long refundAmount = calculator.calculateRefundAmount(payment, request);

    // 조건 추가
    if (refundAmount <= 0) {
      log.warn("환불 금액 0원 → 환불 불가 처리: paymentId={}", payment.getId());
      return;
    }

    // 실제 PortOne Api 호출
    CancelRequest cancelRequest = new CancelRequest();
    cancelRequest.setImpUid(payment.getImpUid());
    cancelRequest.setMerchantUid(payment.getMerchantUid());
    cancelRequest.setReason(request.getReasonCode());
    cancelRequest.setAmount(payment.getAmount()); // PG 호출은 항상 전체 금액 (부분 환불은 디비에만)
    portOnePaymentClient.cancelPayment(cancelRequest);


    // 환불 테이블 생성
    Refund refund = Refund.builder()
            .payment(payment)
            .refundRequest(request)
            .amount(refundAmount) // 디비 저장은 부분 환불로
            .reasonCode(reasonCode)
            .build();

    refundRepository.saveAndFlush(refund);

    // 부분 환불인지 일반 환불인지 ? 처리
    PaymentTransition transition = refundAmount.equals(payment.getAmount()) ? PaymentTransition.REFUND : PaymentTransition.PARTIAL_REFUND;
    paymentStatusChanger.changePayment(payment, transition, payment.getOrderItem().getId(), ActorType.SYSTEM);

    // 일반 결제 (환불 완료 이벤트 발행)
    eventPublisher.publishEvent(new RefundCompletedEvent(
            payment.getOrderItem().getId(),
            null,  // 일반 결제는 구독이 없으므로 null
            refund.getId(),
            reasonCode
    ));

    log.info("환불 실행 완료: paymentId={}, refundAmount={}", payment.getId(), refundAmount);
  }

  // 구독 취소 승인 시에 환불
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void executeRefundForSubscribe(Subscribe subscribe, RefundRequest refundRequest) {
    // 구독 orderitem 기준 가장 최근 결제 조회

    List<Payment> payments = paymentRepository.findByOrderItem(subscribe.getOrderItem());

    if (payments.isEmpty()) { // 결제 없으면 예외 처리
      throw new PaymentNotFoundException(subscribe.getOrderItem().getId());
    }
    // 가장 최근 결제 내역 조회
    Payment latest = payments.get(payments.size() - 1);

    // 자동 환불 조건 처리
    if (isAutoRefund(latest)) {
      log.info("자동 환불 조건 충족 → 구독 결제 전액 환불 처리");
      processAutoRefund(latest, refundRequest);
      return; // 자동 환불 완료 후 종료
    }

    // 보증금 기준 환불 금액 계산 (구독 타입 확인)
    RefundCalculator calculator = getCalculator(latest);
    Long refundAmount = calculator.calculateRefundAmount(latest, refundRequest);

    if (refundAmount <= 0) {
      log.warn("보증금 환불 금액 0원 → 처리 중단: subscribeId={}", subscribe.getId());
      return;
    }

    // 포트 원 환불 요청, 부분환불도 PG에는 전체 금액(latest.getAmount())을 전송하고 디비에는 부분환불 금액으로 저장
    CancelRequest cancelRequest = new CancelRequest();
    cancelRequest.setImpUid(latest.getImpUid());
    cancelRequest.setMerchantUid(latest.getMerchantUid());
    cancelRequest.setReason(refundRequest.getReasonCode()); // 환불 사유
    cancelRequest.setAmount(latest.getAmount()); // PortOne에는 전체 금액 환불 요청

    // 포트원 api 호출
    portOnePaymentClient.cancelPayment(cancelRequest);

    // 나중에 매퍼 이용하기
//    Refund 레코드 생성 (RefundRequest 없이도 기록 가능)
    Refund refund = Refund.builder()
            .payment(latest)
            .refundRequest(refundRequest)
            .amount(refundAmount) // 디비에는 부분 환불 금액 저장
            .reasonCode(refundRequest.getReasonCode())
            .build();
    refundRepository.saveAndFlush(refund);

    // 결제 상태 갱신(가장 최근 결제 표시)
    paymentStatusChanger.changePayment(latest, PaymentTransition.CANCEL_FUTURE_ONLY, subscribe.getOrderItem().getId(), ActorType.SYSTEM); // 보증금만 환불

    // 구독 결제 환불 이벤트 처리
    eventPublisher.publishEvent(new RefundCompletedEvent(
            subscribe.getOrderItem().getId(),
            subscribe.getId(),  // 구독 환불 시에만 존재
            refund.getId(),
            refundRequest.getReasonCode()
    ));

    log.info("구독 환불 실행 완료: subscribeId={}, refundAmount={}", subscribe.getId(), refundAmount);
  }

  // order플랜 타입에 따라 환불 분기 처리
  private RefundCalculator getCalculator(Payment payment) {
    OrderPlanType type = payment.getOrderItem().getOrderPlan().getType();
    return (type == OrderPlanType.RENTAL) ? rentalRefundCalculator : purchaseRefundCalculator;
  }

  // 자동 환불 조건
  private boolean isAutoRefund(Payment payment) {
    try {
      // 결제 24 시간 이내 확인
      if (payment.getRegDate() != null &&
              ChronoUnit.HOURS.between(payment.getRegDate(), LocalDateTime.now()) <= 24) {
        return true;
      }
      // 배송 상태 ready 상태 확인(준비중)
      return deliveryItemRepository.findTopByOrderItemOrderByDelivery_RegDate_Desc(payment.getOrderItem())
              .map(item -> item.getDelivery().getStatus() == DeliveryStatus.READY)
              .orElse(false);
    } catch (Exception e) {
      log.warn("자동 환불 조건 확인 중 오류: {}", e.getMessage());
      return false;
    }
  }

  // 실제 자동 환불 실행
  private void processAutoRefund(Payment payment, RefundRequest request) {
    try {

      CancelRequest cancelRequest = new CancelRequest();
      cancelRequest.setImpUid(payment.getImpUid());
      cancelRequest.setMerchantUid(payment.getMerchantUid());
      cancelRequest.setReason(ReasonCode.AUTO_REFUND); // 자동 환불 처리
      cancelRequest.setAmount(payment.getAmount());

      portOnePaymentClient.cancelPayment(cancelRequest);

      Refund refund = Refund.builder()
              .payment(payment)
              .refundRequest(request)
              .amount(payment.getAmount())
              .reasonCode(ReasonCode.AUTO_REFUND)
              .build();

      refundRepository.saveAndFlush(refund);
      // 결제 상태 전환
      paymentStatusChanger.changePayment(
              payment,
              PaymentTransition.REFUND,
              payment.getOrderItem().getId(),
              ActorType.SYSTEM
      );

      // 자동 환불 이벤트 발행 (구독 여부에 따라 분기)
      Long subscribeId = null;
      try {
        if (payment.getSubscribeRound() != null) {
          subscribeId = payment.getSubscribeRound().getSubscribe().getId();
        }
      } catch (Exception ignored) {}

      eventPublisher.publishEvent(new RefundCompletedEvent(
              payment.getOrderItem().getId(),
              subscribeId, // 구독 환불일 경우만 ID 포함
              refund.getId(),
              ReasonCode.AUTO_REFUND
      ));

      log.info("자동 환불 완료 | paymentId={} | subscribeId={} | amount={} | reason={}",
              payment.getId(),
              subscribeId != null ? subscribeId : "-",
              payment.getAmount(),
              ReasonCode.AUTO_REFUND);

    } catch (Exception e) {
      log.error("자동 환불 처리 실패 | paymentId={} | message={}", payment.getId(), e.getMessage(), e);
      throw new RuntimeException("자동 환불 중 오류 발생", e);
    }

  }

  @Override
  public void cancelRefund(Long refundId) {
    // 필요시 롤백 처리 구현
  }
}

