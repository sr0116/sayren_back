package com.imchobo.sayren_back.domain.delivery.exception;
/** 배송/회수 타임아웃 */
public class DeliveryTimeoutException extends RuntimeException {
  public DeliveryTimeoutException(String message) {
    super(message);
  }
}

