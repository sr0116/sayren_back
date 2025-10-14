package com.imchobo.sayren_back.domain.payment.dto;


import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentType;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
  // 결제 응답 DTO  (어드민/ 일반 회원 공통 사용)
  private Long paymentId;

  private String merchantUid;
  private String impUid;
  private Long amount;
  private PaymentType paymentType;    // 결제 수단
  private PaymentStatus paymentStatus; // 결제 상태
  private String receiptUrl; // 영수증 url
  private LocalDateTime regDate;
  private LocalDateTime voidDate;

  // 상품/ 주문 정보
  private Long orderItemId;
  private String productName;
  private String productImageUrl;
  private Long priceSnapshot;
  private OrderPlanType orderPlanType; // 일반 결제/ 구독

  // 구독 / 회차 정보
  private Long subscribeId;
  private Long roundId;
  private Integer roundNo;
  private PaymentStatus subscribeRoundStatus;
  private LocalDate dueDate;           // 납부 예정일
  private LocalDateTime paidDate;      // 실제 결제 완료일

  // 환불 상태
  private RefundRequestStatus refundStatus;

  // 회원 정보 (관리자 화면 전용)
  private String buyerName;
  private String buyerEmail;
  private String buyerTel;
}
