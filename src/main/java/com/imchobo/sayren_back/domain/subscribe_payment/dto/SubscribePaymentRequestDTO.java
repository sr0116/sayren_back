package com.imchobo.sayren_back.domain.subscribe_payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubscribePaymentRequestDTO {

  @NotNull(message = "구독 ID는 필수입니다.")
  private Long subscribeId;

  @NotNull(message = "결제 ID는 필수입니다.")
  private Long paymentId;

  @NotNull(message = "결제 금액은 필수입니다.")
  @Positive(message = "결제 금액은 양수여야 합니다.")
  private Long amount;

  @NotNull(message = "회차는 필수입니다.")
  @Min(value = 1, message = "최소 1회차 이상이어야 합니다.")
  private Integer roundNo;
}