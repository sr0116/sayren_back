package com.imchobo.sayren_back.domain.board.mapper;

import com.imchobo.sayren_back.domain.board.dto.notice.NoticeCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface NoticeMapper {
  // ============ Create ============
  @Mapping(target = "id", ignore = true)         // PK 자동 생성
  @Mapping(target = "member", ignore = true)     // 로그인 사용자 Service에서 주입
  @Mapping(target = "category", ignore = true)   // NOTICE 카테고리 Service에서 강제 주입
  @Mapping(target = "product", ignore = true)    // Notice는 상품 연관 없음
  @Mapping(target = "status", ignore = true)     // 엔티티 기본값 사용
  Board toEntity(NoticeCreateRequestDTO dto);

  // ============ Update ============
  @Mapping(target = "member", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateEntity(@MappingTarget Board board, NoticeModifyRequestDTO dto);

  // ============ Details ============
  @Mapping(source = "id", target = "boardId")
  @Mapping(source = "category.name", target = "categoryName")
  NoticeDetailsResponseDTO toDetailsDTO(Board board);

  // ============ List ============
  @Mapping(source = "id", target = "boardId")
  NoticeListResponseDTO toListDTO(Board board);
}
