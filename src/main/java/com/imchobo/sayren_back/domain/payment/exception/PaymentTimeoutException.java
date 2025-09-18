package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentTimeoutException extends SayrenException {
  // portone 응답 지연, 네트워크 타임 아웃
  public PaymentTimeoutException(String message) {
    super("PAYMENT_TIMEOUT",message);
  }
}
