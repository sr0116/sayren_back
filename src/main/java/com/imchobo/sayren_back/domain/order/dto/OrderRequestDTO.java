package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
  // 카트아이템 있을 때
  private List<Long> cartItemIds;

  // 카트 아이템 없을 때
  private Long planId;
  private Long productId;

  // 배송지
  private Long addressId;    // 기존 배송지 ID (선택)
}
