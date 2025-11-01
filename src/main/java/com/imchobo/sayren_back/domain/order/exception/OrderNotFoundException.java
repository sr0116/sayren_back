package com.imchobo.sayren_back.domain.order.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;
//주문 없음
public class OrderNotFoundException extends SayrenException {
  public OrderNotFoundException(Long orderId) {
    super("ORDER_NOT_FOUND", "주문 정보를 찾을 수 없습니다. orderId=" + orderId);
  }
}
