package com.imchobo.sayren_back.domain.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
  // 상품 이름
  private String name;
  // 상품 상세설명
  private String description;
  // 상품 가격

  // 상품 판매가능 여부


}
