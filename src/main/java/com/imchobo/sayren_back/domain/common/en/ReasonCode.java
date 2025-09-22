package com.imchobo.sayren_back.domain.common.en;

public enum ReasonCode {
  NONE, // 기본상태(환불없음)
  USER_REQUEST,     // 회원 요청
  AUTO_REFUND, // 24시간 이내 자동 환불
  PRODUCT_DEFECT,   // 상품 불량
  DELIVERY_ISSUE, // 배송 문제(지연, 오배송)
  OUT_OF_STOCK, // 재고 부족
  SERVICE_ERROR,    // 서비스 오류
  SYSTEM_ERROR, // 시스템 장애
  PAYMENT_FAILURE,  // 결제 실패
  CANCEL_REJECTED, // 취소 요청 거절
  CONTRACT_CANCEL,  // 계약 해지 (중도 해지)
  EXPIRED, // 계약 만료(환급)
  OTHER             // 기타
}