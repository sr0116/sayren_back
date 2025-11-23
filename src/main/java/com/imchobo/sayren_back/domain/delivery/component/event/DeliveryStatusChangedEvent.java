package com.imchobo.sayren_back.domain.delivery.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryStatusChangedEvent {
  // 구독, 일반 배송 필요한 배송 이벤트 처리
  private final Long deliveryId;          // 배송 ID
  private final DeliveryStatus status; // 변경된 상태
  private final DeliveryType type; // 배송 유형 (NORMAL, RETURN 등)
  private final Long orderItemId;
}
