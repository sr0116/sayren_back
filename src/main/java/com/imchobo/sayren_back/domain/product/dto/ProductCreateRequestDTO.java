package com.imchobo.sayren_back.domain.product.dto;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachRequestDTO;
import com.imchobo.sayren_back.domain.attach.entity.Attach;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequestDTO {
  // pk
  private Long productId;

  private Long categoryId;

  // 상품 이름
  @NotBlank(message = "상품명은 필수입니다.")
  private String productName;

  // 상품 상세설명
  @NotBlank(message = "상품 설명은 필수입니다.")
  private String description;

  // 상품 가격
  @NotNull(message = "가격은 필수입니다.")
  @Positive(message = "가격은 0보다 커야 합니다.")
  private Integer price;

  // 게시글 생성 여부
  private Boolean isUse;

  // 상품 카테고리 (크롤링 데이터 활용)
  @NotBlank(message = "카테고리는 필수입니다.")
  private String productCategory;

  // 모델명 (시리얼 넘버 역할)
  @NotBlank(message = "모델명은 필수입니다.")
  private String modelName;

  // 수량
  private int stock;
  // 태그
  private Map<String, String> tags;
  // 첨부파일(썸네일)
  private BoardAttachRequestDTO attach;
  // 첨부파일 리스트(데스크립션에 들어가는)
  private List<BoardAttachRequestDTO> attachList;
}
