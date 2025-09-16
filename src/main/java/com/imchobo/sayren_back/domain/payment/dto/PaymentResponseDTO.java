package com.imchobo.sayren_back.domain.payment.dto;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
  // 결제 응답 DTO
  private String merchantUid;
  private String impUid;
  private Long amount;
  private String payType; // 결제 수단
  private PaymentStatus payStatus;
  private String receiptUrl; // 영수증 url
  private LocalDateTime regDate;
}
