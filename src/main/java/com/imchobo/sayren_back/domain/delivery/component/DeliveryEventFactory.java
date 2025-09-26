package com.imchobo.sayren_back.domain.delivery.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.component.event.StatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
/**
 * Delivery 전용 이벤트 팩토리
 * Delivery → 공용 StatusChangedEvent<Delivery> 생성
 */
@Component
public class DeliveryEventFactory {

  public StatusChangedEvent<Delivery> toStatusChangedEvent(
    Delivery delivery,
    Enum<?> oldStatus,
    Enum<?> newStatus,
    ReasonCode reason,
    ActorType actor,
    Map<String, Object> metadata) {

    return StatusChangedEvent.<Delivery>builder()
      .aggregateType("DELIVERY")
      .id(delivery.getId())
      .oldStatus(oldStatus)
      .newStatus(newStatus)
      .reason(reason)
      .actor(actor)
      .changedAt(LocalDateTime.now())
      .snapshot(delivery)
      .metadata(metadata)
      .build();
  }
}