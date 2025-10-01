package com.imchobo.sayren_back.domain.order.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemAddRequestDTO {
  @NotNull
  private Long productId;
  @NotNull
  private Long planId;
}
