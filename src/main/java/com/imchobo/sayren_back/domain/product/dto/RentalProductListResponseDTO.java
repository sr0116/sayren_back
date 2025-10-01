package com.imchobo.sayren_back.domain.product.dto;

import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanResponseDTO;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalProductListResponseDTO {
  // 상품 번호
  private Long productId;

  // 대표 이미지
  private String thumbnailUrl;

  // 상품명
  private String productName;

  // 원가
  private Long price;

  // 최대 혜택가
  private Long benefitPrice;

  // 렌탈 가능 여부
  private Boolean isUse;

  // 모델명
  private String modelName;

  // 태그
  private List<String> tags;

  // 카테고리
  private String productCategory;

  // 설명
  private String description;

  // 연결된 요금제 정보
  private List<OrderPlanResponseDTO> orderPlans;
}
