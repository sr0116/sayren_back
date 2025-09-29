package com.imchobo.sayren_back.domain.payment.calculator;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.exception.DeliveryNotFoundException;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.exception.RefundPolicyViolationException;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
@Component
@RequiredArgsConstructor
@Log4j2
public class RentalRefundCalculator implements RefundCalculator {

  // 일반 구매 환불 기준
  private final DeliveryItemRepository deliveryItemRepository;


  @Override
  public Long calculateRefundAmount(Payment payment, RefundRequest request) {

    // 보증금 스냅샷 가져오기
    OrderItem orderItem = payment.getOrderItem();
    Subscribe subscribe = payment.getSubscribeRound().getSubscribe();
    Long deposit = subscribe.getDepositSnapshot();

    // 배송 정보 조회
    DeliveryItem deliveryItem = deliveryItemRepository
            .findTopByOrderItemOrderByDelivery_RegDate_Desc(payment.getOrderItem())
            .orElseThrow(() -> new DeliveryNotFoundException(payment.getOrderItem().getId()));
    Delivery delivery = deliveryItem.getDelivery();

    // 회수 완료 전에 환불 불가
    if (delivery.getStatus() != DeliveryStatus.RETURNED) {
      throw new RefundPolicyViolationException("회수 완료 전에는 환불 불가");
    }
    // 배송일 기준 경과 일수 계산
    LocalDate deliveredAt = delivery.getModDate().toLocalDate();
    int daysAfterDelivery = (int) ChronoUnit.DAYS.between(deliveredAt, LocalDate.now());

    //  불량/배송 문제 여부 확인 (이때만 환불 가능)
    boolean defective = (request.getReasonCode() == ReasonCode.PRODUCT_DEFECT
            || request.getReasonCode() == ReasonCode.DELIVERY_ISSUE);

    if (defective) {
      log.info("불량 / 배송 문제시 전액 환불");
      return deposit;
    }
    // 렌탈 구매시에 단순 변심 환불 위약금
    if (daysAfterDelivery <= 7) {
      Long deduction = (deposit * 5) / 100; // 상품 전체 금액 기준 5% 차감
      Long refundAmount =  Math.max(deposit - deduction, 0L);
      log.info("렌탈 환불: 변심 7일 이내 → 보증금 {} - 차감 {} = {}", deposit, deduction, refundAmount);
      return Math.max(refundAmount, 0L);
    }
    int totalMonths = orderItem.getOrderPlan().getMonth();
    int remainingMonths = (int) ChronoUnit.MONTHS.between(LocalDate.now(), subscribe.getEndDate());

    // 계약 기간별 위약금률
    int penaltyRate;
    switch (totalMonths) {
      case 24 -> penaltyRate = 10; // 2년
      case 36 -> penaltyRate = 15; // 3년
      case 48 -> penaltyRate = 20; // 4년
      default -> penaltyRate = 15; // 기본값
    }

    Long monthlyFee = subscribe.getMonthlyFeeSnapshot();
    Long penalty = (monthlyFee * remainingMonths * penaltyRate) / 100;

    Long refundAmount = Math.max(deposit - penalty, 0L);
    log.info("렌탈 환불: 변심 7일 초과 → 보증금 {} - 위약금 {} = {}", deposit, penalty, refundAmount);

    return refundAmount;
  }
}
