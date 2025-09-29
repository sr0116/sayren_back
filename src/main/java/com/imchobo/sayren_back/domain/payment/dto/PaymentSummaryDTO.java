package com.imchobo.sayren_back.domain.payment.dto;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class  PaymentSummaryDTO {
  // 결제 요약 DTO (결제 내역 리스트 조회)
  private Long paymentId;
  private Long amount;
  private PaymentStatus paymentStatus;
  private LocalDateTime regDate;

}