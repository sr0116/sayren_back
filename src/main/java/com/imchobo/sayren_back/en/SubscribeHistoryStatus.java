package com.imchobo.sayren_back.en;

public enum SubscribeHistoryStatus {
  PENDING_PAYMENT,  // 결제 대기
  ACTIVE,           // 구독 활성
  PAUSED,           // 일시 중지
  CANCELED,         // 해지됨
  EXPIRED           // 만료됨
}