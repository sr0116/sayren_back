package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundProcessingException extends SayrenException {
  public RefundProcessingException(String message) {
    super("REFUND_PROCESSING_ERROR", "환불 처리 중 오류가 발생했습니다: " + message);
  }
}