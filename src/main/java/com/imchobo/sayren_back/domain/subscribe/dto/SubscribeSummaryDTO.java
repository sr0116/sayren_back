package com.imchobo.sayren_back.domain.subscribe.dto;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscribeSummaryDTO {
  // 이후에 마이페이지에서 구독 현황 리스트 조회시
  private Long subscribeId;
  private SubscribeStatus status;
  private LocalDate startDate;
  private LocalDate endDate;
  private Long monthlyFeeSnapshot;
  private String productName;
  private String productThumbnail;
  private ReasonCode reasonCode;
  private RefundRequestStatus refundRequestStatus;
}