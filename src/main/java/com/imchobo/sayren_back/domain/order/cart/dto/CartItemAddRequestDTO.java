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
  private Long orderPlanId;
}
