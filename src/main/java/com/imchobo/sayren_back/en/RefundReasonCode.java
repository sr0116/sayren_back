package com.imchobo.sayren_back.en;

public enum RefundReasonCode {
  USER_REQUEST,     // 회원 요청 환불
  PRODUCT_DEFECT,   // 상품 불량
  SERVICE_ERROR,    // 서비스 오류
  PAYMENT_FAILURE,  // 결제 실패 후 환불
  CONTRACT_CANCEL,  // 계약 해지
  OTHER             // 기타
}