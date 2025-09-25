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
  // Create
  @Mapping(target = "id", ignore = true)
  @Mapping(source = "productId", target = "product.id")
  @Mapping(target = "member", ignore = true)   // 로그인 사용자 Service에서 주입
  @Mapping(target = "category", ignore = true) // REVIEW 카테고리 Service에서 강제 주입
  @Mapping(target = "status", ignore = true)
  Board toEntity(ReviewCreateRequestDTO dto);

  // Update
  @Mapping(source = "productId", target = "product.id")
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateEntity(@MappingTarget Board board, ReviewModifyRequestDTO dto);

  // Board -> ReviewDetailsResponseDTO
  @Mapping(source = "id", target = "boardId")
  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "category.name", target = "categoryName")
  ReviewDetailsResponseDTO toDetailsDTO(Board board);

  // Board -> ReviewListResponseDTO
  @Mapping(source = "id", target = "boardId")
  @Mapping(source = "product.name", target = "productName")
  ReviewListResponseDTO toListDTO(Board board);
}
