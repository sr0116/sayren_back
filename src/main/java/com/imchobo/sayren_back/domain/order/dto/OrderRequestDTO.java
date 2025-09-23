package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

  private Long memberId;     // 주문자 ID
  private Long addressId;    // 배송지 ID
  private String status;     // 초기 상태 (PENDING 등)

  // 주문 생성 시 필요한 최소 정보만 담음
  // 주문 아이템은 별도 DTO (OrderItemRequestDTO) 리스트로 받을 수 있음
}
