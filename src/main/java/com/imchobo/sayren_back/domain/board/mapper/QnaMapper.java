package com.imchobo.sayren_back.domain.board.mapper;

import com.imchobo.sayren_back.domain.board.dto.qna.QnaCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface QnaMapper {
  // QnaCreateRequestDTO -> Board
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "member", ignore = true)      // 작성자 Service에서 주입
  @Mapping(target = "category", ignore = true)    // 카테고리(QNA)는 Service에서 강제 주입
  @Mapping(target = "product", ignore = true)     // QnA는 상품 연결 없음
  @Mapping(target = "status", ignore = true)
  Board toEntity(QnaCreateRequestDTO dto);

  // QnaModifyRequestDTO -> Board (엔티티 업데이트)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateEntity(@MappingTarget Board board, QnaModifyRequestDTO dto);

  // Board -> QnaDetailsResponseDTO
  @Mapping(source = "id", target = "boardId")
  @Mapping(source = "category.name", target = "categoryName")
  QnaDetailsResponseDTO toDetailsDTO(Board board);

  // Board -> QnaListResponseDTO
  @Mapping(source = "id", target = "boardId")
  QnaListResponseDTO toListDTO(Board board);
}
