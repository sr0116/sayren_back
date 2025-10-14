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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class PurchaseRefundCalculator implements RefundCalculator {

  // 일반 구매 환불 기준
  private final DeliveryItemRepository deliveryItemRepository;


  @Override
  public Long calculateRefundAmount(Payment payment, RefundRequest request) {

    Long baseAmount = payment.getOrderItem().getProductPriceSnapshot();

    if (payment.getRegDate() != null &&
            ChronoUnit.HOURS.between(payment.getRegDate(), LocalDateTime.now()) < 24) {
      log.info("일반 결제 24시간 이내 환불 요청 → 전액 환불");
      return roundUpToTenWon(baseAmount);
    }

    DeliveryItem deliveryItem = deliveryItemRepository
            .findTopByOrderItemOrderByDelivery_RegDate_Desc(payment.getOrderItem())
            .orElseThrow(() -> new DeliveryNotFoundException(payment.getOrderItem().getId()));

    Delivery delivery = deliveryItem.getDelivery();

    // 배송 날짜
    LocalDate deliveredAt = delivery.getModDate().toLocalDate();
    int daysAfterDelivery = (int) ChronoUnit.DAYS.between(deliveredAt, LocalDate.now());

    // 불량/배송 문제 여부 확인 (이때만 환불 가능)
    boolean defective = (request.getReasonCode() == ReasonCode.PRODUCT_DEFECT
            || request.getReasonCode() == ReasonCode.DELIVERY_ISSUE);

    if (defective) {
      log.info("불량 / 배송 문제시 전액 환불");
      return roundUpToTenWon(baseAmount);
    }

    if (daysAfterDelivery <= 7) {
      Long deduction = baseAmount * 5 / 100; // 5% 차감
      Long refundAmount = Math.max(baseAmount - deduction, 0L);
      log.info("단순 변심 7일 이내 환불: 원금 {} - 차감 {} = {}", baseAmount, deduction, refundAmount);
      return roundUpToTenWon(refundAmount);
    }
    log.info("환불: 단순 변심 7일 초과 → 환불 불가");
    throw new RefundPolicyViolationException("단순 변심 환불은 배송 7일 이내만 가능합니다.");
  }

  // 10원 단위 올림
  private long roundUpToTenWon(long value) {
    return ((value + 9) / 10) * 10;
  }
}
