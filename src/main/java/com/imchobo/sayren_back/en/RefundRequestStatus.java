package com.imchobo.sayren_back.en;

public enum RefundRequestStatus {
  REQUESTED,  // 환불 요청됨
  APPROVED,   // 관리자 승인
  REJECTED,   // 관리자 거절
  CANCELED    // 회원이 요청 취소
}