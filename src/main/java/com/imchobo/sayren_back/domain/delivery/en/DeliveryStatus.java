package com.imchobo.sayren_back.domain.delivery.en;

public enum DeliveryStatus {
  READY,         // 배송 대기 (결제 완료 후 자동 생성)
  SHIPPING,      // 배송 중
  DELIVERED,     // 고객에게 도착
  RETURN_READY,  // 회수 준비 (고객 반납 요청 등)
  IN_RETURNING,  // 회수 중
  RETURNED       // 회수 완료
}
