package com.imchobo.sayren_back.domain.product.dto;

import com.imchobo.sayren_back.domain.attach.dto.ProductAttachResponseDTO;
import com.imchobo.sayren_back.domain.order.OrderPlan.dto.OrderPlanResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalProductDetailsResponseDTO {
  // 상품 번호
  private Long productId;

  // 상품 이름
  private String productName;

  // 상품 설명
  private String description;

  // 상품 원가
  private Long price;

  // 최대 혜택가
  private Long benefitPrice;

  // 보증금
  private Long deposit;

  // 렌탈 가능 여부
  private Boolean isUse;

  // 상품 카테고리
  private String productCategory;

  // 모델명
  private String modelName;

  // 등록일
  private LocalDateTime regDate;

  // 현재 재고 수량
  private Integer productStock;

  // 상품 태그 목록
  private List<String> productTags;

  // 첨부파일 목록
  private List<ProductAttachResponseDTO> attachList;

  // 연결된 요금제 정보
  private List<OrderPlanResponseDTO> orderPlans;
}
