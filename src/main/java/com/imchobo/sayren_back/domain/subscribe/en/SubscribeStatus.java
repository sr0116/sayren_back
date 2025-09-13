package com.imchobo.sayren_back.domain.subscribe.en;

public enum SubscribeStatus {
  PENDING_PAYMENT,
  PREPARING,        // 결제 완료, 배송 중
      ACTIVE,           // 구독 진행 중
      ENDED,            // 구독 종료
      CANCELED,         // 중도 해지
      FAILED   // 결제 실패시 끝!
}
