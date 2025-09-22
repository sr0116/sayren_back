package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.BoardCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import jdk.dynalink.linker.LinkerServices;

import java.util.List;

public interface BoardService {
  // 글 등록
  Long register(BoardCreateRequestDTO boardCreateRequestDTO);
  // 글 수정
  void modify(BoardModifyRequestDTO boardModifyRequestDTO);
  // 글 삭제
  void delete(Long boardId);
  // 글 목록
  List<BoardListResponseDTO> list();
  // 글 상세
  BoardDetailsResponseDTO read(Long boardId);

//  List<BoardListResponseDTO> listByCategory(String category);
}
