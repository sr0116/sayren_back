package com.imchobo.sayren_back.domain.order.cart.exception;

 //장바구니 관련 예외의 최상위 클래스

public class CartException extends RuntimeException {
  public CartException(String message) {
    super(message);
  }
}