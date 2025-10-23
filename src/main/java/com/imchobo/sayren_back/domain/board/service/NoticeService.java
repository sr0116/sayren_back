package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeModifyRequestDTO;

import java.util.List;

public interface NoticeService {
  // 공지 등록
  Long register(NoticeCreateRequestDTO dto);

  // 공지 수정
  void modify(Long id, NoticeModifyRequestDTO dto);

  // 공지 삭제
  void delete(Long id);

  // 공지 상세 조회
  NoticeDetailsResponseDTO read(Long id);

  // 공지 목록 조회
  List<NoticeListResponseDTO> list();

  // 페이징 처리
  PageResponseDTO<NoticeListResponseDTO, Board> getList(PageRequestDTO requestDTO);
}
