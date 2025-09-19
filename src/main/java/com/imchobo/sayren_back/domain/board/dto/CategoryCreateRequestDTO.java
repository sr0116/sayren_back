package com.imchobo.sayren_back.domain.board.dto;

import com.imchobo.sayren_back.domain.board.en.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequestDTO {
  // 카테고리 번호(상위)
  private Long parentCategoryId;
  // 카테고리 명
  @NotBlank(message = "카테고리명은 필수입니다.")
  private String name;
  // 카테고리 타입
  @NotNull(message = "카테고리 타입은 필수입니다.")
  private CategoryType type;
}
