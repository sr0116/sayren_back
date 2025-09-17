package com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto;

import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscribePaymentResponseDTO {

  private Long subscribePaymentId;
  private Long subscribeId;
  private Long paymentId;
  private Long amount;
  private PaymentStatus status;
  private Integer roundNo;
  private LocalDateTime regDate;
}
