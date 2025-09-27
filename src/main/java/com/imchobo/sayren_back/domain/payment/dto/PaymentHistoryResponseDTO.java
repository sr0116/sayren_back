package com.imchobo.sayren_back.domain.payment.dto;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponseDTO {

  private Long historyId;
  private Long paymentId;
  private PaymentStatus status;
  private String reasonCode;
  private ActorType actorType;
  private LocalDateTime regDate;
}
