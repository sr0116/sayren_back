package com.imchobo.sayren_back.domain.payment.calculator;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.exception.DeliveryNotFoundException;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundPolicyViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class PurchaseRefundCalculator implements RefundCalculator {

  // 일반 구매 환불 기준
  private final DeliveryItemRepository deliveryItemRepository;


  @Override
  public Long calculateRefundAmount(Payment payment, RefundRequest request) {

    Long amount = payment.getAmount();

    DeliveryItem deliveryItem = deliveryItemRepository
            .findTopByOrderItemOrderByDelivery_RegDate_Desc(payment.getOrderItem())
            .orElseThrow(() -> new DeliveryNotFoundException(payment.getOrderItem().getId()));

    Delivery delivery = deliveryItem.getDelivery();

    if (delivery.getStatus() != DeliveryStatus.RETURNED) {
      throw new RefundPolicyViolationException("회수 완료 전에는 환불 불가");
    }
    // 배송 날짜
    LocalDate deliveredAt = delivery.getModDate().toLocalDate();
    int daysAfterDelivery = (int) ChronoUnit.DAYS.between(deliveredAt, LocalDate.now());

    // 불량/배송 문제 여부 확인 (이때만 환불 가능)
    boolean defective = (request.getReasonCode() == ReasonCode.PRODUCT_DEFECT
            || request.getReasonCode() == ReasonCode.DELIVERY_ISSUE);

    if (defective) {
      log.info("불량 / 배송 문제시 전액 환불");
      return amount;
    }

    if (daysAfterDelivery <= 7) {
      Long deduction = amount * 5 / 100;
      log.info("환불: 단순 변심 7일 이내 → 원금 {} - 차감 {} = {}", amount, deduction, amount - deduction);
      return amount - deduction;
    }
    log.info("환불: 단순 변심 7일 초과 → 환불 불가");
    return 0L;

  }
}
