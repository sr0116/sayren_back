package com.imchobo.sayren_back.domain.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPendingDTO {
  private Long productId;
  private String productName;
  private String modelName;
  private String productCategory;
  private Boolean isUse;
}
