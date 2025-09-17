package com.imchobo.sayren_back.domain.payment.dto;


import com.imchobo.sayren_back.domain.payment.en.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDTO {
  // 결제 요청 DTO
  @NotNull(message = "주문 아이템 ID는 필수입니다.")
  private Long orderItemId;

  @NotNull(message = "결제 금액은 필수 입니다.")
  @Positive(message = "결제 금액은 0보다 커야 합니다.")
  private Long amount;

  @NotNull(message = "결제 타입은 필수 입니다.")
  private PaymentType paymentType;
}
