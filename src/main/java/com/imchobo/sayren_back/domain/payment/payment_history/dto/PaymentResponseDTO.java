package com.imchobo.sayren_back.domain.payment.payment_history.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

  private Long historyId;
  private Long paymentId;
  private String status;
  private String reasonCode;
  private String reasonMessage;
  private String actorType;
  private Long actorId;
  private LocalDateTime regDate;
}
