package com.imchobo.sayren_back.domain.common.en;

import lombok.Getter;

@Getter
public enum ReasonCode {
  NONE,                 // 기본상태(환불없음)
  USER_REQUEST,         // 회원 요청
  AUTO_REFUND,          // 24시간 이내 자동 환불
  REFUND_REQUEST,       // 환불 요청
  REFUND_COMPLETED,     // 환불 완료
  REFUND_FAILED,        // 환불 실패

  PRODUCT_DEFECT,       // 상품 불량
  DELIVERY_ISSUE,       // 배송 문제(지연, 오배송)
  RETURN_REQUEST,       // 회수 요청됨
  RETURN_DELAY,         // 회수 지연
  RETURN_FAILED,        // 회수 실패
  OUT_OF_STOCK,         // 재고 부족

  SERVICE_ERROR,        // 서비스 오류
  SYSTEM_ERROR,         // 시스템 장애

  PAYMENT_FAILURE,      // 결제 실패
  PAYMENT_TIMEOUT,      // 유예 기간 초과

  CANCEL_REJECTED,      // 취소 요청 거절
  CONTRACT_CANCEL,      // 계약 해지 (중도 해지)
  ADMIN_CANCEL,         // 관리자 취소
  ADMIN_FORCE_END,      // 관리자 강제 종료
  ACCOUNT_SUSPENDED,    // 계정 정지
  FRAUD_DETECTED,       // 이상 결제/부정 사용

  EXPIRED,              // 계약 만료(환급)
  OTHER                 // 기타
}