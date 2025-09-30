package com.imchobo.sayren_back.domain.payment.refund.dto;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundCreateDTO {
  // 환불 요청
  @NotNull(message = "결제 ID는 필수입니다.")
  private Long paymentId;

  private Long refundRequestId;

  @NotNull(message = "환불 금액은 필수입니다.")
  @Positive(message = "환불 금액은 양수여야 합니다.")
  private Long amount;

  @NotNull(message = "환불 사유 코드는 필수입니다.")
  private ReasonCode reasonCode;
}
