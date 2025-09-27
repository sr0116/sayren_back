package com.imchobo.sayren_back.domain.subscribe.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeRoundStatusChangedEvent {
  //  so도메인 (결제, 구독만 사요)
  private final Long SubscribeRoundId;
  private final SubscribeRoundTransition transition;   // 상태 + 이유
}