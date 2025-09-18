package com.imchobo.sayren_back.domain.product.dto;

import com.imchobo.sayren_back.domain.attach.dto.AttachResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailsResponseDTO {
  // 상품 번호
  private Long ProductId;

  // 상품 이름
  private String productName;

  // 상품 설명
  private String description;

  // 상품 가격
  private Integer price;

  // 판매 가능 여부
  private Boolean isUse;

  // 상품 카테고리
  private String productCategory;

  // 모델명 (unique)
  private String modelName;

  // 등록일
  private LocalDateTime regDate;

//  // 수정일
//  private LocalDateTime modDate;

  // 현재 재고 수량
  private Integer stock;

  // 상품 태그 목록 (ex: 색상, 사이즈)
  private List<String> tags;

  // 첨부파일 목록 (썸네일, 상세 이미지 등)
  private List<AttachResponseDTO> attachList;
}
