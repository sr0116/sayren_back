package com.imchobo.sayren_back.domain.delivery.exception;
/** 인증/권한 문제 */
public class DeliveryUnauthorizedException extends RuntimeException {
  public DeliveryUnauthorizedException() {
    super("배송 작업에 대한 권한이 없습니다.");
  }
}