package com.imchobo.sayren_back.domain.delivery.exception;
/** 중복 생성 시 */
public class DeliveryAlreadyExistsException extends RuntimeException {
  public DeliveryAlreadyExistsException(Long orderId) {
    super("이미 배송이 존재합니다. orderId=" + orderId);
  }
}
