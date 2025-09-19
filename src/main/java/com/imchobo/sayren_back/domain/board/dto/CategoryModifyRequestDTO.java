package com.imchobo.sayren_back.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryModifyRequestDTO {
  // 수정 카테고리 번호 (어떤 카테고리를 수정할건지)
  @NotNull(message = "카테고리 번호는 필수입니다.")
  private Long id;
  // 변경할 카테고리 명 (이름을 뭘로 바꿀건지)
  @NotBlank(message = "카테고리명은 필수입니다.")
  private String name;
  // 변경할 상위 카테고리 번호 (소속을 바꿀건지)
  private Long parentCategoryId;
}
