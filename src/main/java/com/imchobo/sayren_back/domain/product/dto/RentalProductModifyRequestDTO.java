package com.imchobo.sayren_back.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalProductModifyRequestDTO {
  // 수정 상품 번호
  @NotNull(message = "상품 번호는 필수입니다.")
  private Long productId;

  // 상품명
  @NotBlank(message = "상품명은 필수입니다.")
  private String productName;

  // 설명
  @NotBlank(message = "상품 설명은 필수입니다.")
  private String description;

  // 원가
  @NotNull(message = "원가는 필수입니다.")
  @Positive(message = "원가는 0보다 커야 합니다.")
  private Long price;

  // 최대 혜택가
  private Long benefitPrice;

  // 보증금
  private Long deposit;

  // 렌탈 가능 여부
  private Boolean isUse;

  // 카테고리
  @NotBlank(message = "카테고리는 필수입니다.")
  private String productCategory;

  // 모델명
  @NotBlank(message = "모델명은 필수입니다.")
  private String modelName;

  @NotNull(message = "요금제 ID는 필수입니다.")
  private List<Long> orderPlanIds;
}
