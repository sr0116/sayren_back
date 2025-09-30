package com.imchobo.sayren_back.domain.payment.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
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

}
