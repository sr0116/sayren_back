package com.imchobo.sayren_back.domain.board.repository;

import com.imchobo.sayren_back.domain.board.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
  // 좋아요 카운트
//  int countByBoardId(Long boardId);
}
