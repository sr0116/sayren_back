package com.imchobo.sayren_back.domain.payment.component_recorder;

import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeRoundStatusChangedEvent {
  private final Long SubscribeRoundId;
  private final PaymentStatus status;
}