package com.imchobo.sayren_back.domain.subscribe.en;

public enum SubscribeStatus {
  PENDING_PAYMENT,   // 결제 전
  PREPARING,         // 결제 완료, 배송 준비/중
  ACTIVE,            // 구독 진행 중
  OVERDUE,           // 연체 (자동 결제 실패 누적)
  RETURN_REQUESTED,  // 회수 요청됨
  RETURNED,          // 회수 완료
  ENDED,             // 구독 만료 종료
  CANCEL_REQUESTED,  // 회원 취소 요청
  CANCELED,          // 중도 해지 (승인 시)
  FAILED             // 결제 실패
}