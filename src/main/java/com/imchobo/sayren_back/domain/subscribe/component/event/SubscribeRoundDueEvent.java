package com.imchobo.sayren_back.domain.subscribe.component.event;


import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SubscribeRoundDueEvent extends ApplicationEvent {
  private final SubscribeRound round;
  public SubscribeRoundDueEvent(SubscribeRound round) {
    super(round);
    this.round = round;
  }

}
