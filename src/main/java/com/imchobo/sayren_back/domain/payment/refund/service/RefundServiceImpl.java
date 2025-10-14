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
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundCompletedEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRepository;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeStatusChanger;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
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
  private final SubscribeRepository subscribeRepository;
  private final PaymentStatusChanger paymentStatusChanger;
  private final PortOnePaymentClient portOnePaymentClient;
  private final ApplicationEventPublisher eventPublisher;
  private final DeliveryItemRepository deliveryItemRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final SubscribeStatusChanger subscribeStatusChanger;

  // 일반 결제 환불 처리
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void executeRefund(RefundRequest request, ReasonCode reasonCode) {
    List<Payment> payments = paymentRepository.findByOrderItem(request.getOrderItem());
    if (payments.isEmpty()) throw new PaymentNotFoundException(request.getOrderItem().getId());
    Payment payment = payments.get(0);

    if (refundRepository.existsByPayment(payment)) {
      log.warn("[SKIP] 이미 환불된 결제 → paymentId={}", payment.getId());
      return;
    }

    RefundCalculator calculator = getCalculator(payment);
    Long refundAmount = calculator.calculateRefundAmount(payment, request);

    boolean within24Hours = payment.getRegDate() != null &&
            ChronoUnit.HOURS.between(payment.getRegDate(), LocalDateTime.now()) < 24;
    boolean isAutoRefund = isAutoRefund(request, within24Hours);
    boolean isFullRefund = isFullRefundByPG(request, refundAmount, payment, isAutoRefund);

    PaymentTransition transition = isFullRefund
            ? PaymentTransition.REFUND
            : PaymentTransition.PARTIAL_REFUND;

    CancelRequest cancelRequest = new CancelRequest();
    cancelRequest.setImpUid(payment.getImpUid());
    cancelRequest.setMerchantUid(payment.getMerchantUid());
    cancelRequest.setReason(reasonCode);
    cancelRequest.setAmount(payment.getAmount()); // PG는 항상 전체 금액
    portOnePaymentClient.cancelPayment(cancelRequest);

    Refund refund = Refund.builder()
            .payment(payment)
            .refundRequest(request)
            .amount(refundAmount)
            .reasonCode(reasonCode)
            .build();
    refundRepository.saveAndFlush(refund);

    paymentStatusChanger.changePayment(payment, transition, payment.getOrderItem().getId(), ActorType.SYSTEM);

    eventPublisher.publishEvent(new RefundCompletedEvent(
            payment.getOrderItem().getId(),
            null,
            refund.getId(),
            reasonCode
    ));

    log.info("[{} REFUND] 일반 결제 환불 완료 → paymentId={}, refundAmount={}, isFull={}, isAuto={}",
            transition == PaymentTransition.REFUND ? "FULL" : "PARTIAL",
            payment.getId(),
            refundAmount,
            isFullRefund,
            isAutoRefund);
    log.debug("결제시간={}, 현재시간={}, 경과시간(시)={}, within24h={}, refundAmount={}, paymentAmount={}",
            payment.getRegDate(),
            LocalDateTime.now(),
            ChronoUnit.HOURS.between(payment.getRegDate(), LocalDateTime.now()),
            within24Hours,
            refundAmount,
            payment.getAmount());

  }

  // 구독 결제 환불 처리
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void executeRefundForSubscribe(Subscribe subscribe, RefundRequest refundRequest) {
    Payment depositPayment = paymentRepository.findDepositPayment(subscribe.getOrderItem())
            .orElseThrow(() -> new PaymentNotFoundException(subscribe.getOrderItem().getId()));

    if (refundRepository.existsByPayment(depositPayment)) {
      log.debug("[SKIP] 이미 환불 완료된 구독 결제 → subscribeId={}", subscribe.getId());
      return;
    }

    RefundCalculator calculator = getCalculator(depositPayment);
    Long refundAmount = calculator.calculateRefundAmount(depositPayment, refundRequest);

    boolean within24Hours = depositPayment.getRegDate() != null &&
            ChronoUnit.HOURS.between(depositPayment.getRegDate(), LocalDateTime.now()) < 24;
    boolean isAutoRefund = isAutoRefund(refundRequest, within24Hours);
    boolean isFullRefund = isFullRefundByPG(refundRequest, refundAmount, depositPayment, isAutoRefund);

    PaymentTransition transition = isFullRefund
            ? PaymentTransition.REFUND
            : PaymentTransition.PARTIAL_REFUND;

    CancelRequest cancelRequest = new CancelRequest();
    cancelRequest.setImpUid(depositPayment.getImpUid());
    cancelRequest.setMerchantUid(depositPayment.getMerchantUid());
    cancelRequest.setReason(refundRequest.getReasonCode());
    cancelRequest.setAmount(depositPayment.getAmount());
    portOnePaymentClient.cancelPayment(cancelRequest);

    Refund refund = Refund.builder()
            .payment(depositPayment)
            .refundRequest(refundRequest)
            .amount(refundAmount)
            .reasonCode(refundRequest.getReasonCode())
            .build();
    refundRepository.saveAndFlush(refund);

    paymentStatusChanger.changePayment(depositPayment, transition, subscribe.getOrderItem().getId(), ActorType.SYSTEM);

    eventPublisher.publishEvent(new RefundCompletedEvent(
            subscribe.getOrderItem().getId(),
            subscribe.getId(),
            refund.getId(),
            refundRequest.getReasonCode()
    ));

    log.info("[{} REFUND] 구독 환불 완료 → subscribeId={}, refundAmount={}, isFull={}, isAuto={}",
            transition == PaymentTransition.REFUND ? "FULL" : "PARTIAL",
            subscribe.getId(),
            refundAmount,
            isFullRefund,
            isAutoRefund);
  }


  // 전체 환불 여부 판정 (PG 기준)
  private boolean isFullRefundByPG(RefundRequest request, Long refundAmount, Payment payment, boolean isAutoRefund) {
    if (payment.getAmount() == null) return true;

    // 금액 비교 (Long 안전 비교)
    boolean sameAmount = refundAmount != null && refundAmount.compareTo(payment.getAmount()) >= 0;

    // 불량·배송 문제·24시간 이내·자동 환불 모두 전액 환불 처리
    return isAutoRefund
            || sameAmount
            || request.getReasonCode() == ReasonCode.PRODUCT_DEFECT
            || request.getReasonCode() == ReasonCode.DELIVERY_ISSUE
            || request.getReasonCode() == ReasonCode.CUSTOMER_CANCEL_BEFORE_DELIVERY; // 배송 전 취소 사유 추가
  }

  private boolean isAutoRefund(RefundRequest request, boolean within24Hours) {
    return request.getStatus() == RefundRequestStatus.AUTO_REFUNDED
            || request.getReasonCode() == ReasonCode.AUTO_REFUND
            || within24Hours;
  }

  private RefundCalculator getCalculator(Payment payment) {
    OrderPlanType type = payment.getOrderItem().getOrderPlan().getType();
    return (type == OrderPlanType.RENTAL) ? rentalRefundCalculator : purchaseRefundCalculator;
  }
}
