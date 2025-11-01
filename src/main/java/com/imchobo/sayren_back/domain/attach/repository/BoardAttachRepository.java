package com.imchobo.sayren_back.domain.attach.repository;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardAttachRepository extends JpaRepository<Attach, Long> {
  List<Attach> findByBoardId(Long boardId);

}
