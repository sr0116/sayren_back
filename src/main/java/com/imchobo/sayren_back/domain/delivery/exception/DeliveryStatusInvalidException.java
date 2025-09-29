package com.imchobo.sayren_back.domain.delivery.exception;
/** 잘못된 상태 전환 시 */
public class DeliveryStatusInvalidException extends RuntimeException {
  public DeliveryStatusInvalidException(String message) {
    super(message);
  }
}