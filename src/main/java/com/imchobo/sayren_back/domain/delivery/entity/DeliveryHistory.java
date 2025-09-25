package com.imchobo.sayren_back.domain.delivery.entity;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class DeliveryHistory {
  private Long id;
  private Long deliveryId;
  private String oldStatus;
  private String newStatus;
  private ReasonCode reason;
  private ActorType actor;
  private LocalDateTime changedAt;

  public static DeliveryHistory of(Long deliveryId, String oldStatus, String newStatus,
                                   ReasonCode reason, ActorType actor, LocalDateTime changedAt) {
    return DeliveryHistory.builder()
      .deliveryId(deliveryId)
      .oldStatus(oldStatus)
      .newStatus(newStatus)
      .reason(reason)
      .actor(actor)
      .changedAt(changedAt)
      .build();
  }
}
