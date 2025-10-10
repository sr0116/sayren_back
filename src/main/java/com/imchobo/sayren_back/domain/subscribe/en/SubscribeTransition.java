package com.imchobo.sayren_back.domain.subscribe.en;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import lombok.Getter;

@Getter
public enum SubscribeTransition {
  // 결제
  PENDING_PAYMENT(SubscribeStatus.PENDING_PAYMENT, ReasonCode.NONE), // 결제 전 대기 상태

  PREPARE(SubscribeStatus.PREPARING, ReasonCode.NONE),      // 결제 완료 → 배송 준비
  FAIL_PAYMENT(SubscribeStatus.FAILED, ReasonCode.PAYMENT_FAILURE), // 결제 실패


  // 구독 시작 전 배송 전 취소
  PREPARE_CANCEL_REQUEST(SubscribeStatus.PREPARING, ReasonCode.USER_REQUEST), // 배송 시작 전 사용자가 취소 요청
  PREPARE_CANCEL_APPROVE(SubscribeStatus.CANCELED, ReasonCode.CONTRACT_CANCEL), //배송 전 취소 승인 (계약 취소)
  // 구독 시작 전 -> 배송 완료후에 시작
  DELIVERY_IN_PROGRESS(SubscribeStatus.PREPARING, ReasonCode.NONE), // 배송 중
  START(SubscribeStatus.ACTIVE, ReasonCode.NONE),           // 배송 완료 → 구독 활성화

  // 회원 취소 요청 및 관리자 승인 여부
  REQUEST_CANCEL(SubscribeStatus.ACTIVE, ReasonCode.USER_REQUEST),     // 회원 취소 요청 (ACTIVE 유지)
  CANCEL_APPROVE(SubscribeStatus.ACTIVE, ReasonCode.USER_REQUEST),     // 관리자 승인 (ACTIVE 유지)
  CANCEL_REJECT(SubscribeStatus.ACTIVE, ReasonCode.CANCEL_REJECTED),          // 관리자 거절 (ACTIVE 유지) // 관리자 거절

  // 회수
  RETURN_REQUEST(SubscribeStatus.ACTIVE, ReasonCode.RETURN_REQUEST),   // 회수 요청됨 (ACTIVE 유지)
  RETURN_IN_PROGRESS(SubscribeStatus.ACTIVE, ReasonCode.NONE),         // 회수 진행 중 (ACTIVE 유지)
  RETURN_DELAY(SubscribeStatus.ACTIVE, ReasonCode.RETURN_DELAY),       // 회수 지연 (ACTIVE 유지)
  RETURN_FAILED(SubscribeStatus.ACTIVE, ReasonCode.RETURN_FAILED),     // 회수 실패 (ACTIVE 유지)
  RETURNED_ONLY(SubscribeStatus.ENDED, ReasonCode.RETURN_REQUEST), // 아직 회수만 완료 , 계약 종료 전
  RETURNED_AND_CANCELED(SubscribeStatus.CANCELED, ReasonCode.CONTRACT_CANCEL), // 회수 및 종료

  // 구독 종료
  END(SubscribeStatus.ENDED, ReasonCode.EXPIRED),

  // 연체 관련 (유예기간 및 최종 연체)
  OVERDUE_PENDING(SubscribeStatus.OVERDUE, ReasonCode.PAYMENT_FAILURE), //  결제 실패 후 3일 유예 중
  OVERDUE_FINAL(SubscribeStatus.OVERDUE, ReasonCode.PAYMENT_TIMEOUT),   // 유예기간 종료 후 최종 연체 확정
  OVERDUE(SubscribeStatus.OVERDUE, ReasonCode.PAYMENT_FAILURE), // 연체 종료

  // 자동 결제 실패 → 연체
  ADMIN_FORCE_END(SubscribeStatus.CANCELED, ReasonCode.ADMIN_FORCE_END); // 관리자 강제 종료  // 회수 완료

  private final SubscribeStatus status;
  private final ReasonCode reason;

  SubscribeTransition(SubscribeStatus status, ReasonCode reason) {
    this.status = status;
    this.reason = reason;
  }
}
