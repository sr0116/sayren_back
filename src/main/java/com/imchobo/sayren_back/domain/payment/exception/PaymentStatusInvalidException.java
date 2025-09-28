package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentStatusInvalidException extends SayrenException {
  // 이미 결제 완료나 환불 상태에서 다시 재시도 하는 경우
  public PaymentStatusInvalidException(String status) {
    super("PAYMENT_STATUS_INVALID", "해당 결제 상태에서는 처리할 수 없습니다. status = " + status);
  }
}
