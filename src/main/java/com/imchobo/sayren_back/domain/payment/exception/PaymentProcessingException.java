package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentProcessingException extends SayrenException {
  // DB 저장, PortOne Api 호출 중 알 수 없는 내부 오류
  public PaymentProcessingException(String message) {
    super("PAYMENT_PROCESSING_ERROR",  message);
  }
}
