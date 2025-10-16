package com.imchobo.sayren_back.domain.delivery.component.event;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;




// 배송 상태 변경 이벤트 (구독/결제 등 다른 도메인에서 사용)
//  deliveryId : 어떤 배송인지
// orderItemId : 구독 연결용 (OrderItem 단위로 구독이 매핑되기 때문)
// status : 변경된 배송 상태
@Getter
@AllArgsConstructor
public class StatusChangedEvent {
  private final Long deliveryId;     // 배송 PK
  private final Long orderItemId;    // 구독 연결용 (order_item_id)
  private final DeliveryStatus status; // 변경된 배송 상태
}