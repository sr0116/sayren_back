package com.imchobo.sayren_back.domain.order.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;


//이미 취소된 주문
public class OrderAlreadyCanceledException extends SayrenException {
  public OrderAlreadyCanceledException(Long orderId) {
    super("ORDER_ALREADY_CANCELED", "이미 취소된 주문입니다. orderId=" + orderId);
  }
}
