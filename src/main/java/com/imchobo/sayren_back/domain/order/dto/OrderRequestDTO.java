package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

  private Long addressId;    // 배송지 ID
  private String status;     // 초기 상태 (PENDING 등)

  // 주문 생성 시 필요한 최소 정보만 담음
  // 주문자는 memberId 대신, SecurityContextHolder에서 가져오기
}

