package com.imchobo.sayren_back.domain.order.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemAddRequestDTO {

  @NotNull
  private Long productId; // 상품 ID

  @NotNull
  private String type; // 요금제 타입 (PURCHASE, RENTAL 등)

  private Integer month; // 렌탈일 경우 개월 수 (null 가능)
}
