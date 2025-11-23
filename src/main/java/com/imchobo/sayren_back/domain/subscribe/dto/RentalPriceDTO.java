package com.imchobo.sayren_back.domain.subscribe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentalPriceDTO {
  private Long monthlyFee;
  private Long deposit;
  private Long totalPrice;
}
