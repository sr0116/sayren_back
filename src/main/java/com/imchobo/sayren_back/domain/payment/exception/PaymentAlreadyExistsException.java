package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentAlreadyExistsException extends SayrenException {
  // 동일한 merchantUid로 중복 요청시 발생 예외 처리
  public PaymentAlreadyExistsException(String merchantUid) {
    super("PAYMENT_ALREADY_EXISTS", "이미 처리된 결제입니다. merchantUid = " + merchantUid);
  }
}
