package com.imchobo.sayren_back.domain.subscribe.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeStatusChangedEvent {
  // 내부 도메인 이벤트(구독, 결제 도메인만 사용)
  private final Long subscribeId;
  private final SubscribeTransition transition;
  private final ActorType actor;
}