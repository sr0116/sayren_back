package com.imchobo.sayren_back.domain.order.exception;
//잘못된 상태 전환

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class InvalidOrderStatusException extends SayrenException {
  public InvalidOrderStatusException(String message) {
    super("INVALID_ORDER_STATUS", message);
  }
}