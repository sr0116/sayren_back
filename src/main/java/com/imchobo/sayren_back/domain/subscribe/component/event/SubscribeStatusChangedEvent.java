package com.imchobo.sayren_back.domain.subscribe.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeStatusChangedEvent {
  private final Long subscribeId;
  private final SubscribeTransition transition;
  private final ActorType actor;

}
