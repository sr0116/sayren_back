package com.imchobo.sayren_back.domain.payment.refund.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class RefundAmountExceededException extends SayrenException {
  public RefundAmountExceededException(Long requestAmount, Long paymentAmount) {
    super("REFUND_AMOUNT_EXCEEDED",
            "환불 금액(" + requestAmount + ")이 결제 금액(" + paymentAmount + ")을 초과했습니다.");
  }
}