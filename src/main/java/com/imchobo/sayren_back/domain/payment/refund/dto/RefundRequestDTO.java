package com.imchobo.sayren_back.domain.payment.refund.dto;


import com.imchobo.sayren_back.domain.common.en.ActorType;
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

  private Long paymentId;          // 어떤 결제 건 환불 요청인지
  private ReasonCode reasonCode;   // 환불 사유 코드 (USER_REQUEST, PRODUCT_DEFECT 등)

  // 선택 필드
  private String description;      // 상세 사유 (사용자 입력용)
  private Long orderItemId;        // (선택) orderItemId 직접 전달 가능
}
