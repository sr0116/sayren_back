package com.imchobo.sayren_back.domain.payment.refund.dto;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestResponseDTO {
  // 환불 응답 DTO (서버 → 클라이언트)

  private Long refundRequestId;       // 환불 요청 PK
  private Long paymentId;
  private Long orderItemId;             // 주문 PK
  private RefundRequestStatus status; // 환불 요청 상태 (REQUESTED, CANCELED 등)
  private ReasonCode reasonCode;      // 사유 코드
  private LocalDateTime regDate;      // 요청일
  private LocalDateTime voidDate;     // 취소일 (있으면)

  // 추가 필드
  private OrderPlanType orderPlanType;          // 일반/구독 구분
  private String productName;         // 상품명 (스냅샷 or orderItem에서 가져오기)
  private String memberName;           // (관리자 전용) 요청자
  private String memberEmail;          // (관리자 전용) 요청자 이메일

  // 썸네일 추가
  private String productThumbnail;
}
