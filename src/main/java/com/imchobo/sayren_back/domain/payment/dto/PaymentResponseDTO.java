package com.imchobo.sayren_back.domain.payment.dto;


import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
  // 결제 응답 DTO
  private Long paymentId;
  private Long orderItemId;
  private String merchantUid;
  private String impUid;
  private Long amount;
  private PaymentType paymentType;    // 결제 수단
  private PaymentStatus paymentStatus; // 결제 상태
  private String receiptUrl; // 영수증 url
  private LocalDateTime regDate;
  private LocalDateTime voidDate;
  private String productName;
  private Long priceSnapshot;
  private OrderPlanType orderPlanType;

  private boolean refundRequested;
}
