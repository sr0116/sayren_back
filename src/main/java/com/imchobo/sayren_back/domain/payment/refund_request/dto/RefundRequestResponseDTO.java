package com.imchobo.sayren_back.domain.payment.refund_request.dto;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund_request.en.RefundRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestResponseDTO {
  // 환불 응답 DTO (서버 → 클라이언트)

  private Long refundRequestId;      // PK
  private Long orderItemId;          // FK
  private RefundRequestStatus status;
  private ReasonCode reasonCode;
  private LocalDateTime regDate;      // 요청 생성일시
  private LocalDateTime voidDate;
}
