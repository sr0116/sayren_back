package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewModifyRequestDTO;

import java.util.List;

public interface ReviewService {
  // 게시글 등록
  Long register(ReviewCreateRequestDTO dto);

  // 게시글 수정
  void modify(Long id, ReviewModifyRequestDTO dto);

  // 게시글 삭제
  void delete(Long id);

  // 게시글 단일 조회
  ReviewDetailsResponseDTO read(Long id);

  // 게시글 목록 조회
  List<ReviewListResponseDTO> list();

  // 페이징 처리
  PageResponseDTO<ReviewListResponseDTO, Board> getList(PageRequestDTO requestDTO);
}
