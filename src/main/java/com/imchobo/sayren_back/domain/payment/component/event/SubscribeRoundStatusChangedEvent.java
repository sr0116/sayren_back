package com.imchobo.sayren_back.domain.payment.component.event;

import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeRoundStatusChangedEvent {
  private final Long SubscribeRoundId;
  private final PaymentStatus status;
}