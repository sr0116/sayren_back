package com.imchobo.sayren_back.domain.delivery.component.event;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
// 배송 상태 변경 이벤트
//  deliveryId : 어떤 배송인지
// orderItemId : 구독 연결용
// status : 변경된 배송 상태
@Getter
@AllArgsConstructor
public class StatusChangedEvent {
  private final Long deliveryId;     // 배송 PK
  private final Long orderItemId;    // 구독 연결용
  private final DeliveryStatus status; // 변경된 배송 상태
}