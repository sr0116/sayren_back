package com.imchobo.sayren_back.domain.product.dto;

import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.common.en.CommonStatus;
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
  private Long price;
  // 모델명
  private String modelName;
  // 태그
  private List<String> tags;
  // 게시글 상태
  private CommonStatus status;

  // 상품 카테고리
  private String category;
}
