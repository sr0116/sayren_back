package com.imchobo.sayren_back.domain.board.mapper;

import com.imchobo.sayren_back.domain.board.dto.review.ReviewCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface ReviewMapper {
  // ReviewCreateRequestDTO -> Board (등록할 때)
  @Mapping(target = "id", ignore = true)              // PK는 자동 생성
  @Mapping(source = "productId", target = "product.id") // productId 값 → product 객체의 id에 매핑
  @Mapping(target = "member", ignore = true)          // 로그인 회원은 Service에서 주입
  @Mapping(target = "category", ignore = true)        // 카테고리(REVIEW)는 Service에서 강제 주입
  @Mapping(target = "status", ignore = true)          // 상태 기본값은 엔티티에서 설정
  Board toEntity(ReviewCreateRequestDTO dto);

  // ReviewModifyRequestDTO -> Board (수정할 때, 엔티티 업데이트)
  @Mapping(source = "productId", target = "product.id") // productId 다시 매핑
  @Mapping(target = "category", ignore = true)          // 카테고리는 수정 시 변경하지 않음
  @Mapping(target = "member", ignore = true)            // 작성자 변경 불가
  @Mapping(target = "status", ignore = true)            // 상태는 Service에서 관리
  void updateEntity(@MappingTarget Board board, ReviewModifyRequestDTO dto);

  // Board -> ReviewDetailsResponseDTO (상세 조회 응답)
  @Mapping(source = "id", target = "boardId")           // Board PK → boardId
  @Mapping(source = "product.id", target = "productId") // 연관 객체 product.id → productId
  @Mapping(source = "product.name", target = "productName") // product.name → productName
  @Mapping(source = "category.name", target = "categoryName") // 카테고리명 → categoryName
  ReviewDetailsResponseDTO toDetailsDTO(Board board);

  // Board -> ReviewListResponseDTO (목록 조회 응답)
  @Mapping(source = "id", target = "boardId")           // Board PK → boardId
  @Mapping(source = "product.name", target = "productName") // product.name → productName
  ReviewListResponseDTO toListDTO(Board board);
}
