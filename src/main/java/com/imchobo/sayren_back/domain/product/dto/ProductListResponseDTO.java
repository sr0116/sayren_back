package com.imchobo.sayren_back.domain.product.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponseDTO {
  // 상품 번호
  private Long productId;
  // 대표 이미지 (attach에서 isThumbnail= 'true' 꺼내오기)
  private String thumbnailUrl;
  // 상품명
  private String productName;
  // 가격
  private Integer price;
  // 판매 여부
  private Boolean isUse;
//  // 모델명
//  private String modelName;
//  // 태그
//  private List<String> tags;

}
