package com.imchobo.sayren_back.domain.payment.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class PaymentAmountMismatchException extends SayrenException {
  // portone 응답금액 vs 주문 금액 불일치일
  public PaymentAmountMismatchException(Long expected, Long actual) {
    super("PAYMENT_AMOUNT_MISMATCH",
            "결제 금액 불일치: expected=" + expected + ", actual=" + actual);
  }
}
