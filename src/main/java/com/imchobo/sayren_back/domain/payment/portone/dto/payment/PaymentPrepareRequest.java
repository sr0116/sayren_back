package com.imchobo.sayren_back.domain.payment.portone.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentPrepareRequest {

  @NotBlank(message = "merchantUid는 필수입니다.")
  @JsonProperty("merchant_uid")
  private String merchantUid;

  @NotNull(message = "결제 금액은 필수입니다.")
//  @Positive(message = "결제 금액은 0보다 커야 합니다.")
  private Long amount;
}
