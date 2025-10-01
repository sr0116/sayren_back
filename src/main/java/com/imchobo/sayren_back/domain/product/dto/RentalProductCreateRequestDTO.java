package com.imchobo.sayren_back.domain.product.dto;

import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanResponseDTO;
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
public class RentalProductCreateRequestDTO {
  // 상품 이름
  @NotBlank(message = "상품명은 필수입니다.")
  private String productName;

  // 상품 상세설명
  @NotBlank(message = "상품 설명은 필수입니다.")
  private String description;

  // 상품 원가
  @NotNull(message = "원가는 필수입니다.")
  @Positive(message = "원가는 0보다 커야 합니다.")
  private Long price;

  // 최대 혜택가
  private Long benefitPrice;

  // 보증금
  private Long deposit;

  // 렌탈 가능 여부
  private Boolean isUse;

  // 상품 카테고리
  @NotBlank(message = "카테고리는 필수입니다.")
  private String productCategory;

  // 모델명
  @NotBlank(message = "모델명은 필수입니다.")
  private String modelName;

  @NotNull(message = "요금제 ID는 필수입니다.")
  private List<Long> orderPlanIds;
}
