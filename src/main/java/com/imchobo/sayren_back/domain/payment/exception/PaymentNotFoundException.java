package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentNotFoundException extends SayrenException {
  // 결제 아이디 및 UID 조회 실패
  public PaymentNotFoundException(Long paymentId) {
    super("PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다. paymentId = " + paymentId);
  }

  public PaymentNotFoundException(String merchantUid) {
    super("PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다. merchantUid = " + merchantUid);
  }
}
