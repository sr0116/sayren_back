package com.imchobo.sayren_back.domain.subscribe.en;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Getter;
// 나중에 스케줄링 처리예정(3일 동안 결제 안하면 어떻게 할지)

@Getter
public enum SubscribeRoundTransition {

  // 회차 단위 (각각 하나의 회차)
  PAY_SUCCESS(PaymentStatus.PAID, ReasonCode.NONE),              // 회차 결제 성공
  PAY_FAIL(PaymentStatus.FAILED, ReasonCode.PAYMENT_FAILURE),    // PortOne 결제 실패
  PAY_TIMEOUT(PaymentStatus.FAILED, ReasonCode.PAYMENT_TIMEOUT), // 내부 스케줄링 판단
  CANCEL(PaymentStatus.REFUNDED, ReasonCode.AUTO_REFUND),        // 환불 처리
  RETRY_SUCCESS(PaymentStatus.PAID, ReasonCode.NONE),            // 실패 후 재시도 성공
  RETRY_FAIL(PaymentStatus.FAILED, ReasonCode.PAYMENT_FAILURE),  // 실패 후 재시도 실패

  // 결제 실패 후 재시도 대기 상태 (3일간 유예)
  RETRY_PENDING(PaymentStatus.PENDING, ReasonCode.PAYMENT_FAILURE), // 유예기간 중, 재시도 가능

  // 전체 단위 (구독 영향 포함 - 전체 회차)
  INIT_FAIL(PaymentStatus.FAILED, ReasonCode.PAYMENT_FAILURE),   // 1회차 실패 → 구독 자체 실패
  CANCEL_ALL(PaymentStatus.REFUNDED, ReasonCode.CONTRACT_CANCEL), // 전체 환불/취소
  FORCED_END(PaymentStatus.FAILED, ReasonCode.PAYMENT_TIMEOUT);   // 미납으로 전체 강제 종료

  private final PaymentStatus status;
  private final ReasonCode reason;

  SubscribeRoundTransition(PaymentStatus status, ReasonCode reason) {
    this.status = status;
    this.reason = reason;
  }
}

