package com.imchobo.sayren_back.domain.delivery.sharedevent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeliveryStatusChangedEvent {
  private final Long deliveryId;      // 어떤 배송인지
  private final DeliveryStatus status; // 변경된 상태
  private final DeliveryType type;    // DELIVERY or RETURN
  private final Long memberId;        // 회원 ID (구독 찾을 때 필요할 수 있음)
}
