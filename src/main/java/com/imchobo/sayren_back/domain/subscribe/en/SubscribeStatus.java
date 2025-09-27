package com.imchobo.sayren_back.domain.subscribe.en;

public enum SubscribeStatus {
  PENDING_PAYMENT,   // 결제 대기
  PREPARING,         // 결제 완료, 배송 준비/중 (아직 구독 시작 전)
  ACTIVE,            // 구독 진행 중 (배송 완료 시점부터)
  OVERDUE,           // 자동 결제 실패 → 연체
  ENDED,             // 계약 기간 만료 → 종료
  CANCELED,          // 중도 해지 (회수 완료 시점 등)
  FAILED             // 결제 실패
}