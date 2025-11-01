package com.imchobo.sayren_back.domain.attach.mapper;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachRequestDTO;
import com.imchobo.sayren_back.domain.attach.dto.BoardAttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.dto.ProductAttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.entity.Attach;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.util.MappingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingUtil.class)
public interface BoardAttachMapper {
  // Entity -> 게시판 DTO
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isThumbnail", constant = "false")
  @Mapping(target = "board", source = "board")
  @Mapping(target = "product", ignore = true)
  Attach toBoardAttach(BoardAttachRequestDTO dto, Board board);

}
