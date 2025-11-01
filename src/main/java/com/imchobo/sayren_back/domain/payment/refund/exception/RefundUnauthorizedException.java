package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundUnauthorizedException extends SayrenException {
  public RefundUnauthorizedException() {
    super("REFUND_UNAUTHORIZED", "환불을 실행할 권한이 없습니다.");
  }
}