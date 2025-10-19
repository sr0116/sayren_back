package com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto;

import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SubscribeRoundResponseDTO {

  private Long subscribeRoundId;
  private Long subscribeId;

  private Long amount;
  private Integer roundNo;
  private PaymentStatus payStatus;

  private LocalDate dueDate;
  private LocalDateTime paidDate;
  private LocalDateTime regDate;


}
