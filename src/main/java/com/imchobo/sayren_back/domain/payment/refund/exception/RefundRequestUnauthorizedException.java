package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundRequestUnauthorizedException extends SayrenException {
  public RefundRequestUnauthorizedException() {
    super("REFUND_REQUEST_UNAUTHORIZED", "해당 환불 요청에 대한 권한이 없습니다.");
  }
}