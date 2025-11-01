package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundRequestNotFoundException extends SayrenException {
  public RefundRequestNotFoundException(Long refundRequestId) {
    super("REFUND_REQUEST_NOT_FOUND", "환불 요청을 찾을 수 없습니다. 요청 아이디 : " + refundRequestId);
  }
}
