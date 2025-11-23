package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundRequestAlreadyExistsException extends SayrenException {
  public RefundRequestAlreadyExistsException(Long paymentId) {
    super("REFUND_REQUEST_ALREADY_EXISTS",
            "해당 결제 건(" + paymentId + ")에 대해 이미 환불 요청이 접수되었습니다.");
  }
}