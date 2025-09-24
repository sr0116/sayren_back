package com.imchobo.sayren_back.domain.board.mapper;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface BoardMapper {
  // 등록 DTO → 엔티티
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "product", ignore = true)   // BoardRequestDTO에는 product 없음
  @Mapping(target = "status", ignore = true)
  Board toEntity(BoardRequestDTO dto);

  // 수정 DTO → 엔티티 업데이트
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateEntity(@MappingTarget Board board, BoardRequestDTO dto);

  // 엔티티 → 응답 DTO
  @Mapping(source = "id", target = "boardId")
  @Mapping(source = "category.name", target = "categoryName")
  BoardResponseDTO toResponseDTO(Board board);
}
