package com.imchobo.sayren_back.domain.common.util;

import com.imchobo.sayren_back.domain.exentity.Order;
import com.imchobo.sayren_back.domain.exentity.OrderItem;
import com.imchobo.sayren_back.domain.exentity.OrderPlan;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe_payment.entity.SubscribePayment;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class MappingUtil {
  // 필수 fk 매핑 매서드

  @Named("mapPayment")
  public Payment paymentIdToEntity(Long paymentId) {
    if (paymentId == null) throw new IllegalArgumentException("paymentId가 null입니다.");
    return Payment.builder().id(paymentId).build();
  }

  @Named("mapOrder")
  public Order orderIdToEntity(Long orderId) {
    if (orderId == null) {
      throw new IllegalArgumentException("orderId가 null입니다.");
    }
    return Order.builder().id(orderId).build();
  }

  @Named("mapOrderItem")
  public OrderItem orderItemIdToEntity(Long orderItemId) {
    if (orderItemId == null) throw new IllegalArgumentException("orderItemId가 null입니다.");
    return OrderItem.builder().id(orderItemId).build();
  }

  @Named("mapSubscribe")
  public Subscribe subscribeIdToEntity(Long subscribeId) {
    if (subscribeId == null) throw new IllegalArgumentException("subscribeId가 null입니다.");
    return Subscribe.builder().id(subscribeId).build();
  }

  @Named("mapSubscribePayment")
  public SubscribePayment subscribePaymentIdToEntity(Long subscribePaymentId) {
    if (subscribePaymentId == null) throw new IllegalArgumentException("subscribePaymentId가 null입니다.");
    return SubscribePayment.builder().id(subscribePaymentId).build();
  }

  @Named("mapOrderPlan")
  public OrderPlan orderPlanIdToEntity(Long planId) {
    return planId != null ? OrderPlan.builder().id(planId).build() : null;
  }

  // === 공통 변환 ===
  @Named("toStringSafe")
  public String toStringSafe(Object value) {
    return value != null ? value.toString() : null;
  }

  @Named("toLongSafe")
  public Long toLongSafe(Object value) {
    if (value instanceof Number num) return num.longValue();
    if (value != null) return Long.parseLong(value.toString());
    return null;
  }
}
