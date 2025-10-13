package com.imchobo.sayren_back.domain.subscribe.dto;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class SubscribeResponseDTO {
  // 구독 신청/조회 응답에 내려줄 데이터
  private Long subscribeId;
  private Long orderItemId;
  private SubscribeStatus status;
  private ReasonCode reasonCode;
  private RefundRequestStatus refundRequestStatus;

  private Long monthlyFeeSnapshot;
  private Long depositSnapshot;
  private Integer totalMonths;

  private LocalDateTime regDate; // 신청일
  private LocalDate startDate; // 구독 시작일
  private LocalDate endDate;

  // 멤버 관련
  private String memberName;
  private String memberEmail;
  private String productName;
  private String productCategory;

}
