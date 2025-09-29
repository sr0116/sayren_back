package com.imchobo.sayren_back.domain.order.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

//장바구니 비어있음
public class EmptyCartException extends SayrenException {
  public EmptyCartException(Long memberId) {
    super("EMPTY_CART", "장바구니가 비어있습니다. memberId=" + memberId);
  }
}
