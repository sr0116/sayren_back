package com.imchobo.sayren_back.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalProductModifyRequestDTO {
  // 수정 상품 번호 (PK)
  @NotNull(message = "상품 번호는 필수입니다.")
  private Long productId;

  // 변경할 상품 이름
  @NotBlank(message = "상품명은 필수입니다.")
  private String productName;

  // 변경할 상품 설명
  @NotBlank(message = "상품 설명은 필수입니다.")
  private String description;

  // 변경할 상품 가격
  @NotNull(message = "상품 가격은 필수입니다.")
  @Positive(message = "상품 가격은 0보다 커야 합니다.")
  private Integer price;

  // 판매 가능 여부
  private Boolean isUse;

  // 변경할 상품 카테고리
  @NotBlank(message = "카테고리는 필수입니다.")
  private String productCategory;

  // 변경할 모델명
  @NotBlank(message = "모델명은 필수입니다.")
  private String modelName;
}
