package com.imchobo.sayren_back.domain.board.dto;

import com.imchobo.sayren_back.domain.board.en.CategoryType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
  // 카테고리 번호
  private Long id;
  // 상위 카테고리 번호
  private Long parentCategoryId;
  // 카테고리 명
  private String name;
  // 카테고리 타입
  private CategoryType type;

}
