package com.imchobo.sayren_back.domain.order.cart.exception;

  //동일한 상품이 이미 장바구니에 존재할 때 발생하는 예외

public class CartAlreadyExistsException extends CartException {
  public CartAlreadyExistsException(Long productId) {
    super("이미 장바구니에 존재하는 상품입니다. productId=" + productId);
  }
}