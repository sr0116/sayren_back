package com.imchobo.sayren_back.domain.order.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
  private Long orderItemId; // 주문아이템 ID
  private Long productId; // 상품 ID
  private Long planId; // 요금제 ID
  private Integer productPriceSnapshot; // 주문 당시 가격
}