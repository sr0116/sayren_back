package com.imchobo.sayren_back.domain.subscribe.component.event;


import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeRoundStatusChangedEvent {
  private final Long subscribeRoundId;
  private final SubscribeRoundTransition transition;

}
