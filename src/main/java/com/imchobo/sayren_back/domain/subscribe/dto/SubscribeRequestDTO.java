package com.imchobo.sayren_back.domain.subscribe.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SubscribeRequestDTO {
  // 구독 신청 시 클라이언트에게 보내는 데이터
  // 나중에 수정 예정
  @NotNull(message = "주문 아이템 ID는 필수입니다.")
  private Long orderItemId;

  // 멤버 나중에 jwt 나 context에서 받아올거라 지워야 함

  @NotNull(message = "월 렌탈료는 필수입니다.")
  @PositiveOrZero(message = "월 렌탈료는 0 이상이어야 합니다.")
  private Long monthlyFeeSnapshot;

  @NotNull(message = "보증금은 필수입니다.")
  @PositiveOrZero(message = "보증금은 0 이상이어야 합니다.")
  private Long depositSnapshot;

  @NotNull(message = "구독 개월 수는 필수입니다.")
  @Min(value = 12, message = "구독 개월 수는 1년 이상이어야 합니다.")
  private Integer totalMonths;

  @NotNull(message = "구독 시작일은 필수입니다.")
  @FutureOrPresent(message = "시작일은 오늘 또는 미래여야 합니다.")
  private LocalDate startDate;

  @NotNull(message = "구독 종료일은 필수입니다.")
  @Future(message = "종료일은 미래여야 합니다.")
  private LocalDate endDate;
}