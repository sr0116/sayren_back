package com.imchobo.sayren_back.domain.common.en;

public enum ReasonCode {
  NONE, // 기본상태(환불없음)
  USER_REQUEST,     // 회원 요청 환불
  PRODUCT_DEFECT,   // 상품 불량
  SERVICE_ERROR,    // 서비스 오류
  PAYMENT_FAILURE,  // 결제 실패 후 환불
  CANCEL_REJECTED, // 취소 요청 거절
  CONTRACT_CANCEL,  // 계약 해지
  OTHER             // 기타
}