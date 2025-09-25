package com.imchobo.sayren_back.domain.subscribe.component.event;

import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeStatusChangedEvent {
  private final Long subscribeId;
  private final SubscribeStatus status;
}