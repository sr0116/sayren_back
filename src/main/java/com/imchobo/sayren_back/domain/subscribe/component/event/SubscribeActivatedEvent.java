package com.imchobo.sayren_back.domain.subscribe.component.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

@Getter
public class SubscribeActivatedEvent {
  private final Long subscribeId;
  private final LocalDate startDate;

  public SubscribeActivatedEvent(Long subscribeId, LocalDate startDate) {
    this.subscribeId = subscribeId;
    this.startDate = startDate;
  }
}
