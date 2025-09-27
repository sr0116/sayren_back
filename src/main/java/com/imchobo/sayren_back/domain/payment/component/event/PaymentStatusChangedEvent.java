package com.imchobo.sayren_back.domain.payment.component.event;

import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentStatusChangedEvent {
  private final Long paymentId;
  private final PaymentStatus status;
  private  final Long orderItemId;
}