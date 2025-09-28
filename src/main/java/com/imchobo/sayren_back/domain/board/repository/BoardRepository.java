package com.imchobo.sayren_back.domain.board.repository;

import com.imchobo.sayren_back.domain.board.dto.faq.FaqListResponseDTO;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
  // 카테고리 타입으로 게시글 찾기
  List<Board> findByCategoryType(CategoryType type);

  // 페이징
  Page<Board> findByCategoryType(CategoryType type, Pageable pageable);
}
