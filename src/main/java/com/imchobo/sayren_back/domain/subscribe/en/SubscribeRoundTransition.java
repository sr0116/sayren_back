package com.imchobo.sayren_back.domain.subscribe.en;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Getter;
// 나중에 스케줄링 처리예정(3일 동안 결제 안하면 어떻게 할지)

@Getter
public enum SubscribeRoundTransition {

  PAY_SUCCESS(PaymentStatus.PAID, ReasonCode.NONE),              // 회차 결제 성공
  PAY_FAIL(PaymentStatus.FAILED, ReasonCode.PAYMENT_FAILURE),    // PortOne 결제 실패
  PAY_TIMEOUT(PaymentStatus.FAILED, ReasonCode.PAYMENT_FAILURE), // 유예기간 초과 미납
  CANCEL(PaymentStatus.FAILED, ReasonCode.AUTO_REFUND);          // 환불 승인 시 남은 회차 취소

  private final PaymentStatus status;
  private final ReasonCode reason;

  SubscribeRoundTransition(PaymentStatus status, ReasonCode reason) {
    this.status = status;
    this.reason = reason;
  }
}

