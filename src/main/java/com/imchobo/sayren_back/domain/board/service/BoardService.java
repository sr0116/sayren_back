package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
//import com.imchobo.sayren_back.domain.board.dto.BoardListResponseDTO;
//import com.imchobo.sayren_back.domain.board.dto.BoardModifyRequestDTO;

import java.util.List;

public interface BoardService {
  // 게시글 등록
  Long register(BoardRequestDTO dto);

  // 게시글 수정
  void modify(Long id, BoardRequestDTO dto);

  // 게시글 삭제
  void delete(Long id);

  // 게시글 단건 조회
  BoardResponseDTO read(Long id);

  // 게시글 목록 조회
  List<BoardResponseDTO> list();
}
