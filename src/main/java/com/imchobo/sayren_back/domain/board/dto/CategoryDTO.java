package com.imchobo.sayren_back.domain.board.dto;

import com.imchobo.sayren_back.domain.board.en.CategoryType;
import lombok.*;
import org.hibernate.annotations.AnyKeyJavaClass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
  // 카테고리 번호
  private Long id;
  // 상위 카테고리 번호 (리뷰 -> TV/냉장고/세탁기 등 대분류/소분류 나뉠 때 사용)
  private Long parentCategoryId;
  // 카테고리명
  private String name;
  // 카테고리 타입
  private CategoryType type;
}
