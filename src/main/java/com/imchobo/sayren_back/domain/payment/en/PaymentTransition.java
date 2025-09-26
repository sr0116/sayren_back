package com.imchobo.sayren_back.domain.payment.en;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentInfoResponse;
import lombok.Getter;
// 상태 변환에 필요


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.portone.dto.payment.PaymentInfoResponse;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

// 결제 상태 전환 Enum
 //  PortOne 응답(PaymentInfoResponse)을 기반으로 PaymentStatus/ReasonCode를 결정

@Log4j2
@Getter
public enum PaymentTransition {
  COMPLETE(PaymentStatus.PAID, ReasonCode.NONE),             // 정상 결제 완료
  FAIL_USER(PaymentStatus.FAILED, ReasonCode.USER_REQUEST),  // 사용자가 결제 취소
  FAIL_SYSTEM(PaymentStatus.FAILED, ReasonCode.SYSTEM_ERROR),// 시스템 장애
  FAIL_PAYMENT(PaymentStatus.FAILED, ReasonCode.PAYMENT_FAILURE), // 잔액 부족, 한도 초과
  FAIL_TIMEOUT(PaymentStatus.FAILED, ReasonCode.PAYMENT_TIMEOUT), // 유예기간 초과 미납
  REFUND(PaymentStatus.REFUNDED, ReasonCode.AUTO_REFUND),    // 환불 확정
  PARTIAL_REFUND(PaymentStatus.PARTIAL_REFUNDED, ReasonCode.USER_REQUEST); // 부분 환불

  private final PaymentStatus status;
  private final ReasonCode reason;

  PaymentTransition(PaymentStatus status, ReasonCode reason) {
    this.status = status;
    this.reason = reason;
  }

  // PortOne 응답 기반 → Transition 매핑
  public static PaymentTransition fromPortOne(PaymentInfoResponse info) {
    String status = info.getStatus();
    String errorMsg = info.getFailReason() != null ? info.getFailReason().toLowerCase() : "";
    String errorCode = info.getErrorCode() != null ? info.getErrorCode().toLowerCase() : "";

    // 1. 정상 결제 완료
    if ("paid".equalsIgnoreCase(status)) {
      return COMPLETE;
    }

    // 2. 결제 실패
    if ("failed".equalsIgnoreCase(status)) {
      // 사용자 취소
      if (errorMsg.contains("사용자 취소") || errorCode.equals("f400")) {
        return FAIL_USER;
      }

      // 잔액 부족, 한도 초과
      if (errorMsg.contains("잔액") || errorMsg.contains("한도") || errorCode.equals("f410")) {
        return FAIL_PAYMENT;
      }

      // 유예기간 초과 (예: 미납)
      if (errorMsg.contains("유예") || errorMsg.contains("timeout")) {
        return FAIL_TIMEOUT;
      }

      // 그 외 → 시스템 장애 처리
      return FAIL_SYSTEM;
    }

    // 3. 기타 예상 못한 상태
    return FAIL_SYSTEM;
  }

}