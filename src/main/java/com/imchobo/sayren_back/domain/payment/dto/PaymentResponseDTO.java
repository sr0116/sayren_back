package com.imchobo.sayren_back.domain.payment.dto;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
  private Long paymentId;
  private Long orderId;
  private Long memberId;
  private String merchantUid;
  private String impUid;
  private Long amount;
  private String payType;
  private PaymentStatus payStatus;   // PENDING, PAID, FAILED, REFUNDED, 부분환불까디
  private String receiptUrl;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
