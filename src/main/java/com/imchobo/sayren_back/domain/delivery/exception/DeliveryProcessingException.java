package com.imchobo.sayren_back.domain.delivery.exception;
/** 알 수 없는 처리 오류 */
public class DeliveryProcessingException extends RuntimeException {
  public DeliveryProcessingException(String message) {
    super(message);
  }
}