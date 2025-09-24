package com.imchobo.sayren_back.domain.delivery.sharedevent;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryStatusChangedEvent {
  private final Long deliveryId;
  private final DeliveryStatus newStatus;
  private final DeliveryType type;
  private final Long memberId;
}
