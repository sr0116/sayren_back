package com.imchobo.sayren_back.domain.payment.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentStatusChangedEvent {
  private final Long paymentId;
  private final PaymentTransition transition;
  private  final Long orderItemId;
  private final ActorType actor;
}