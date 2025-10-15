package com.imchobo.sayren_back.domain.order.en;

public enum OrderStatus {

  PENDING,   // 결제 대기
  PAID,      // 주문 완료 (→ 이후 배송, 구독은 개별 모듈에서 상태 관리)
  CANCELED  // 주문 취소 (결제 실패/사용자 취소/관리자 취소 포함)
}
