package com.imchobo.sayren_back.domain.board.mapper;

import com.imchobo.sayren_back.domain.board.dto.faq.FaqCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface FaqMapper {
  // FaqCreateRequestDTO -> Board
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "category", ignore = true)   // 카테고리 FAQ는 Service에서 주입
  @Mapping(target = "product", ignore = true)    // FAQ는 상품 연관 없음
  @Mapping(target = "status", ignore = true)
  Board toEntity(FaqCreateRequestDTO dto);

  // FaqModifyRequestDTO -> Board 업데이트
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateEntity(@MappingTarget Board board, FaqModifyRequestDTO dto);

  // Board -> FaqDetailsResponseDTO
  @Mapping(source = "id", target = "boardId")
  @Mapping(source = "category.name", target = "categoryName")
  FaqDetailsResponseDTO toDetailsDTO(Board board);

  // Board -> FaqListResponseDTO
  @Mapping(source = "id", target = "boardId")
  FaqListResponseDTO toListDTO(Board board);
}
