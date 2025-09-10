package com.imchobo.sayren_back.domain.payment.dto;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentSummaryDTO {
  private Long paymentId;
  private Long amount;
  private PaymentStatus status;   // PENDING, PAID, FAILED, REFUNDED, 부분환불까디
  private LocalDateTime regDate;

}
