package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqModifyRequestDTO;

import java.util.List;

public interface FaqService {
  // FAQ 등록
  Long register(FaqCreateRequestDTO dto);

  // FAQ 수정
  void modify(Long id, FaqModifyRequestDTO dto);

  // FAQ 삭제
  void delete(Long id);

  // FAQ 상세 조회
  FaqDetailsResponseDTO read(Long id);

  // FAQ 목록 조회
  List<FaqListResponseDTO> list();

  // 페이징 처리
  PageResponseDTO<FaqListResponseDTO, Board> getList(PageRequestDTO requestDTO);
}
