package com.imchobo.sayren_back.domain.delivery.exception;
/** 배송 정보 없음 */
public class DeliveryNotFoundException extends RuntimeException {
  public DeliveryNotFoundException(Long id) {
    super("배송 정보를 찾을 수 없습니다. id=" + id);
  }
}