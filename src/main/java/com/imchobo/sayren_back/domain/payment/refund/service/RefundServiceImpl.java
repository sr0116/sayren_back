package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.payment.calculator.PurchaseRefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RefundCalculator;
import com.imchobo.sayren_back.domain.payment.calculator.RentalRefundCalculator;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


  //  환불 결제 처리
  @Transactional
  @Override
  public void executeRefund(RefundRequest request, ReasonCode reasonCode) {
    List<Payment> payments = paymentRepository.findByOrderItem(request.getOrderItem());
    if (payments.isEmpty()) {
      throw new PaymentNotFoundException(request.getOrderItem().getId());
    }
    Payment payment = payments.get(payments.size() - 1); // 최근 결제
    // 결제 유형 (주문 플랜에 따른 환불 계산기)
    RefundCalculator calculator = getCalculator(payment);
    // 환불 금액 계산
    Long refundAmount = calculator.calculateRefundAmount(payment, request);

    // 나중에 매퍼로 대체 가능하면 수정
    Refund refund = Refund.builder()
            .payment(payment)
            .refundRequest(request)
            .amount(refundAmount)
            .reasonCode(reasonCode)
            .build();

    refundRepository.save(refund);

    // Payment 상태 변경 (이것도 나중에 옵션 트렌지션으로 만들기)
    payment.setPaymentStatus(PaymentStatus.REFUNDED);

    log.info("환불 실행 완료: paymentId={}, refundAmount={}", payment.getId(), refundAmount);
  }

  // order플랜 타입에 따라 환불 분기 처리
  private RefundCalculator getCalculator(Payment payment) {
    OrderPlanType type = payment.getOrderItem().getOrderPlan().getType();
    return (type == OrderPlanType.RENTAL) ? rentalRefundCalculator : purchaseRefundCalculator;
  }
  // 구독 취소 승인 시에 환불
  @Transactional
  @Override
  public void executeRefundForSubscribe(Subscribe subscribe, ReasonCode reasonCode) {
    // 구독 orderitem 기준 가장 최근 결제 조회
    List<Payment> payments = paymentRepository.findByOrderItem(subscribe.getOrderItem());
    if (payments.isEmpty()) { // 결제 없으면 예외 처리
      throw new PaymentNotFoundException(subscribe.getOrderItem().getId());
    }
    Payment latest = payments.get(payments.size() - 1);
    // 분기 처리
    RefundRequest refundRequest = new RefundRequest();
    refundRequest.setReasonCode(reasonCode); // 환불 사유
    Long refundAmount = rentalRefundCalculator.calculateRefundAmount(latest,refundRequest );
    // 나중에 매퍼 이용하기
//    Refund 레코드 생성 (RefundRequest 없이도 기록 가능)
    Refund refund = Refund.builder()
            .payment(latest)
            .refundRequest(null) // 구독 취소 흐름은 RefundRequest 엔티티 없이 진행 (취소라서)
            .amount(refundAmount)
            .reasonCode(reasonCode)
            .build();
    refundRepository.save(refund);

    // 4) 결제 상태 갱신(가장 최근 결제 표시). 필요 시 '관련 모든 회차 결제'에 표기하는 확장도 가능.
    latest.setPaymentStatus(PaymentStatus.REFUNDED);
    log.info("구독 환불 실행 완료: subscribeId={}, refundAmount={}", subscribe.getId(), refundAmount);
  }

  @Override
  public void cancelRefund(Long refundId) {
    // 필요시 롤백 처리 구현
  }
}

