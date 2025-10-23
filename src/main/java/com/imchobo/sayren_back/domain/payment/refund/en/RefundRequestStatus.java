package com.imchobo.sayren_back.domain.payment.refund.en;

public enum RefundRequestStatus {
  PENDING,
  AUTO_REFUNDED,
  APPROVED,
  APPROVED_WAITING_RETURN,
  REJECTED,
  CANCELED // 취소까지 고려할지 생각중
}
