package com.imchobo.sayren_back.domain.board.mapper;

import com.imchobo.sayren_back.domain.board.dto.BoardCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface BoardMapper {
  // 게시글 등록 (DTO -> Entity)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "product", ignore = true)   // 매퍼에서는 무시
  @Mapping(target = "status", ignore = true)
  Board toEntity(BoardCreateRequestDTO dto);

  // 수정 DTO → Entity
  @Mapping(source = "id", target = "id")         // 수정할 게시글 번호 매핑
  @Mapping(target = "member", ignore = true)     // 작성자는 수정 불가 → Service에서 그대로 둠
  @Mapping(target = "category", ignore = true)   // 카테고리도 Service에서 필요시 주입
  @Mapping(target = "product", ignore = true)    // 상품 연관관계는 수정 시 따로 처리
  @Mapping(target = "status", ignore = true)     // 상태는 Service에서 변경할 때만 조작
  Board toEntity(BoardModifyRequestDTO dto);

  // Board -> BoardListResponseDTO
  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "category.name", target = "categoryName") // Category 엔티티에서 name 추출
  @Mapping(source = "regDate", target = "regDate") // BaseEntity의 등록일
  BoardListResponseDTO toListDTO(Board board);

  // Board → BoardDetailsResponseDTO
  @Mapping(source = "id", target = "boardId")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "secret", target = "isSecret")
  @Mapping(source = "category.name", target = "categoryName")   // 카테고리명
  @Mapping(source = "product.name", target = "productName")     // 상품명
  @Mapping(source = "regDate", target = "regDate")
  @Mapping(source = "modDate", target = "modDate")
  BoardDetailsResponseDTO toDetailsDTO(Board board);


}
