package com.imchobo.sayren_back.domain.subscribe.dto;

import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data

public class SubscribeResponseDTO {
  // 구독 신청/조회 응답에 내려줄 데이터
  // 나중에 수정 예정
  private Long subscribeId;
  private Long orderItemId;
  private SubscribeStatus status;
  private Integer monthlyFeeSnapshot;
  private Integer depositSnapshot;
  private Integer totalMonths;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDateTime regdate;
  private LocalDateTime moddate;


}
