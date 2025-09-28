package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundPolicyViolationException extends SayrenException {
  public RefundPolicyViolationException(String message) {
    super("REFUND_POLICY_VIOLATION", "환불 정책 위반: " + message);
  }
}