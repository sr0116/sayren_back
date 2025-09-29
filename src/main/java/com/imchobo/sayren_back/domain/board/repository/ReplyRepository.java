package com.imchobo.sayren_back.domain.board.repository;

import com.imchobo.sayren_back.domain.board.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
  // 댓글 카운트
  // int countByBoardId(Long boardId);
}
