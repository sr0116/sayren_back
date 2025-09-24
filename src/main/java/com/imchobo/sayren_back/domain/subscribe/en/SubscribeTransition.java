package com.imchobo.sayren_back.domain.subscribe.en;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import lombok.Getter;

@Getter
public enum SubscribeTransition {
  PREPARE(SubscribeStatus.PREPARING, ReasonCode.NONE),      // 결제 완료 → 배송 준비
  FAIL_PAYMENT(SubscribeStatus.FAILED, ReasonCode.PAYMENT_FAILURE), // 결제 실패
  START(SubscribeStatus.ACTIVE, ReasonCode.NONE),           // 배송 완료 → 구독 활성화
  REQUEST_CANCEL(SubscribeStatus.CANCEL_REQUESTED, ReasonCode.USER_REQUEST), // 회원 취소 요청
  CANCEL(SubscribeStatus.CANCELED, ReasonCode.USER_REQUEST), // 회수 완료 → 구독 해지
  CANCEL_REJECT(SubscribeStatus.ACTIVE, ReasonCode.CANCEL_REJECTED), // 관리자 거절
  END(SubscribeStatus.ENDED, ReasonCode.EXPIRED);           // 만료 → 구독 종료

  private final SubscribeStatus status;
  private final ReasonCode reason;

  SubscribeTransition(SubscribeStatus status, ReasonCode reason) {
    this.status = status;
    this.reason = reason;
  }
}
