package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentUnauthorizedException extends SayrenException {
  // 결제 회원이랑 실제 로그인한 회원 정보가 다를때
  public PaymentUnauthorizedException() {
    super("PAYMENT_UNAUTHORIZED", "결제 접근 권한이 없습니다.");
  }
}
