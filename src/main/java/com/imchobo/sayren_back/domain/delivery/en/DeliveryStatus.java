package com.imchobo.sayren_back.domain.delivery.en;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DeliveryStatus {
  READY,         // 배송 대기
  SHIPPING,      // 배송 중
  DELIVERED,     // 고객에게 도착
  RETURN_READY,  // 회수 준비
  IN_RETURNING,  // 회수 중
  RETURNED;      // 회수 완료

  @JsonCreator
  public static DeliveryStatus from(String value) {
    try {
      return DeliveryStatus.valueOf(value.toUpperCase());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid DeliveryStatus value: " + value);
    }
  }
}
