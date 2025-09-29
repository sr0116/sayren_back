package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundAlreadyProcessedException extends SayrenException {
  public RefundAlreadyProcessedException(Long paymentId) {
    super("REFUND_ALREADY_PROCESSED", "이미 환불이 처리된 결제입니다. Payment ID: " + paymentId);
  }
}