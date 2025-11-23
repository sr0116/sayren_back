package com.imchobo.sayren_back.domain.order.component.event;

import lombok.Value;
// 주문이 성공적으로 저장되고 커밋된 후 발행되는 이벤트
@Value
public class OrderPlacedEvent {
  Long orderId; // 주문 식별자만 전달
}