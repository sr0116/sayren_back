package com.imchobo.sayren_back.domain.order.cart.exception;

// 특정 장바구니 아이템을 찾을 수 없을 때 발생하는 예외

public class CartNotFoundException extends RuntimeException {

  public CartNotFoundException(String message) {
    super(message);
  }
  public CartNotFoundException(Long id) {
    super("장바구니 아이템을 찾을 수 없습니다. ID: " + id);
  }
}
