package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {

  private Long orderItemId;       // 주문 아이템 PK
  private Long productId;         // 상품 ID
  private String productName;     // 상품명
  private Integer priceSnapshot;  // 주문 시점 가격
  private Long planId;            // 요금제 ID (null 가능)
}
