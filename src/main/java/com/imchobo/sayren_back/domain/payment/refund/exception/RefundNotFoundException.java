package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundNotFoundException extends SayrenException {
  public RefundNotFoundException(Long id) {
    super("REFUND_NOT_FOUND", "환불 내역을 찾을 수 없습니다. 환불 ID: " + id);
  }
}