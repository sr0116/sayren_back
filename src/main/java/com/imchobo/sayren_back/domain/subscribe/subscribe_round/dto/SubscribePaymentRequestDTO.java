package com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscribePaymentRequestDTO {

  @NotNull(message = "구독 ID는 필수입니다.")
  private Long subscribeId;

  @NotNull(message = "결제 금액은 필수입니다.")
  @Positive(message = "결제 금액은 양수여야 합니다.")
  private Long amount;

  @NotNull(message = "회차는 필수입니다.")
  @Min(value = 1, message = "최소 1회차 이상이어야 합니다.")
  private Integer roundNo;

  @NotNull(message = "납부 예정일은 필수입니다.")
  @FutureOrPresent(message = "납부 예정일은 오늘 이후여야 합니다.")
  private LocalDate dueDate;
}