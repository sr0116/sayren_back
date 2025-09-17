package com.imchobo.sayren_back.domain.payment.refund_request.dto;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {
  // 환불 요청 DTO (클라이언트 → 서버)

  @NotNull(message = "주문 아이템 ID는 필수입니다.")
  private Long orderItemId;

  @NotNull(message = "환불 사유 코드는 필수입니다.")
  private ReasonCode reasonCode;
}
