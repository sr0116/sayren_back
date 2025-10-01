package com.imchobo.sayren_back.domain.order.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
  private Long cartItemId;   // 장바구니 PK
  private Long productId;    // 상품 PK
  private String productName; // 상품명
  private Long planId;       // 요금제 PK (일반구매면 null)
  private String planType;   // PURCHASE/RENTAL
  private Long price;        // 상품 단가 (스냅샷 or 현재가)
}