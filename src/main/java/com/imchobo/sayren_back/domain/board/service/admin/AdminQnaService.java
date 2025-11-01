package com.imchobo.sayren_back.domain.board.service.admin;

import com.imchobo.sayren_back.domain.board.dto.qna.QnaCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;

import java.util.List;

public interface AdminQnaService {
  // qna 등록
  Long register(QnaCreateRequestDTO dto);
  // qna 수정
  void modify(Long id, QnaModifyRequestDTO dto);
  // 삭제
  void delete(Long id);
  // 단일 조회
  QnaDetailsResponseDTO read(Long id);
  // 목록 조회
  List<QnaListResponseDTO> list();

  // 페이징 처리
  PageResponseDTO<QnaListResponseDTO, Board> getList(PageRequestDTO requestDTO);

}
