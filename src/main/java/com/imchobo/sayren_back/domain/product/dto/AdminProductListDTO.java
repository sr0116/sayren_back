package com.imchobo.sayren_back.domain.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProductListDTO {
  Long id;
  String name;
  Long price;
  Boolean isUse;
  String productCategory;
  String modelName;

  String thumbnail;
}
