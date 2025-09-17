package com.imchobo.sayren_back.domain.board.repository;

import com.imchobo.sayren_back.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
