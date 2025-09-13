package com.imchobo.sayren_back.domain.payment.en;

public enum PaymentStatus {
  PENDING,
  PAID,
  FAILED,
  REFUNDED,
  PARTIAL_REFUNDED // 부분 환불
}
