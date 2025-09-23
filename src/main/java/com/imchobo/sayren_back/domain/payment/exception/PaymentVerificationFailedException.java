package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentVerificationFailedException extends SayrenException {
  // PortOne Api 검증 실패 예외 처리(impUid 불일치, 금액 불일치 등)
  public PaymentVerificationFailedException(String message) {
    super("PAYMENT_VERIFICATION_FAILED",  message);
  }
}
