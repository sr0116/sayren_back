package com.imchobo.sayren_back.domain.payment.refund.dto;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
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
  private ReasonCode reasonCode;
  private LocalDateTime regDate;
}
