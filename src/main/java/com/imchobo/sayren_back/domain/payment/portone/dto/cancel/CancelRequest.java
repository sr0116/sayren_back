package com.imchobo.sayren_back.domain.payment.portone.dto.cancel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest {
  //환불 dto
  @JsonProperty("imp_uid")
  @NotBlank(message = "impUid는 필수입니다.")
  private String impUid;

  @JsonProperty("merchant_uid")
  @NotBlank(message = "merchantUid는 필수입니다.")
  private String merchantUid;

  @NotBlank(message = "환불 사유는 필수입니다.")
  private String reason;

  @NotBlank(message = "환불 금액은 필수입니다.")
  @Positive(message = "환불 금액은 0보다 커야 합니다.")
  private Long amount; // 가격
}