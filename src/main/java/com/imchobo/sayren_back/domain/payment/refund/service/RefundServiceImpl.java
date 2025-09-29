package com.imchobo.sayren_back.domain.payment.refund.service;

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

  @Transactional
  @Override
  public void executeRefund(RefundRequest request, ReasonCode reasonCode) {
    List<Payment> payments = paymentRepository.findByOrderItem(request.getOrderItem());
    if (payments.isEmpty()) {
      throw new PaymentNotFoundException(request.getOrderItem().getId());
    }

    Payment payment = payments.get(payments.size() - 1); // 최근 결제
    RefundCalculator calculator = getCalculator(payment);

    Long refundAmount = calculator.calculateRefundAmount(payment, request);

    Refund refund = Refund.builder()
            .payment(payment)
            .refundRequest(request)
            .amount(refundAmount)
            .reasonCode(reasonCode)
            .build();

    refundRepository.save(refund);

    // Payment 상태 변경
    payment.setPaymentStatus(PaymentStatus.REFUNDED);

    log.info("환불 실행 완료: paymentId={}, refundAmount={}", payment.getId(), refundAmount);
  }

  private RefundCalculator getCalculator(Payment payment) {
    if (payment.getOrderItem().getOrderPlan().getType() == OrderPlanType.RENTAL) {
      return rentalRefundCalculator;
    } else {
      return purchaseRefundCalculator;
    }
  }

  @Override
  public void cancelRefund(Long refundId) {
    // 필요시 롤백 처리 구현
  }
}

