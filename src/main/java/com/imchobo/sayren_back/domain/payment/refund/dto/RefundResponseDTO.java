package com.imchobo.sayren_back.domain.payment.refund.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDTO {
  // 환불 응답
  private Long refundId;
  private Long paymentId;
  private Long amount;
  private String reason;
  private LocalDateTime regDate;
}
