package com.imchobo.sayren_back.domain.order.cart.exception;

 //회원의 장바구니가 비어 있을 때 발생하는 예외

public class CartEmptyException extends CartException {
  public CartEmptyException(Long memberId) {
    super("회원의 장바구니가 비어있습니다. memberId=" + memberId);
  }
}