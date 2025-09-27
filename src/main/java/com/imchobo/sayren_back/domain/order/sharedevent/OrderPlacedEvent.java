package com.imchobo.sayren_back.domain.order.sharedevent;

import lombok.Value;

/**
 * 주문이 성공적으로 저장되고 커밋된 후 발행되는 이벤트
 * 배송 자동 생성을 트리거하기 위해 최소 정보만 담는다
 */
@Value
public class OrderPlacedEvent {
  Long orderId;
  Long memberId;
}
