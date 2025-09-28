package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundRequestStatusInvalidException extends SayrenException {
  public RefundRequestStatusInvalidException(String status) {
    super("REFUND_REQUEST_STATUS_INVALID", "잘못된 환불 요청 상태입니다. 현재 상태: " + status);
  }
}