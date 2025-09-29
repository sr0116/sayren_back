package com.imchobo.sayren_back.domain.payment.calculator;

import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.exception.DeliveryNotFoundException;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;

@RequiredArgsConstructor
@Log4j2
public class PurchaseRefundCalculator implements RefundCalculator {

  // 일반 구매 환불 기준
  private final DeliveryItemRepository deliveryItemRepository;


  @Override
  public Long calculateRefundAmount(Payment payment, OrderItem orderItem, RefundRequest request) {

    Long amount = payment.getAmount();
    DeliveryItem deliveryItem = deliveryItemRepository.findFirstByOrderItem(payment.getOrderItem())
            .orElseThrow(() -> new DeliveryNotFoundException(payment.getOrderItem().getId()));

    Delivery delivery = deliveryItem.getDelivery();


    return 0L;
  }
}
