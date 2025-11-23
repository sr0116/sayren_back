package com.imchobo.sayren_back.domain.payment.en;

public enum PaymentStatus {
  PENDING,
  PAID,
  FAILED,
  CANCELED, // 구독 회차 종료 처리
  REFUNDED,
  PARTIAL_REFUNDED; // 부분 환불;

  // 나중에
  // pg 사는 소문자로 응답 (서비스 로직에서 쉽게 사용하려면 switch 사용으로 해주기)
  public static PaymentStatus fromPortOneStatus(String status) {
    if (status == null) {
      return FAILED; // 결제 실패
    }
    switch (status.toLowerCase()) {
      case "ready":
      case "scheduled":
        return PENDING; // 결제 대기
      case "paid":
        return PAID;
      case "cancelled":
        return REFUNDED;
      case "partial_cancelled":
        return PARTIAL_REFUNDED;
      case "failed":
      default:
        return FAILED;
    }
  }

}
