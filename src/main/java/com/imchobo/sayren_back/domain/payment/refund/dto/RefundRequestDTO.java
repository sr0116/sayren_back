package com.imchobo.sayren_back.domain.payment.refund.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {
  // 환불 요청
  private Long paymentId;
  private Long amount;
  private String reason;
}
