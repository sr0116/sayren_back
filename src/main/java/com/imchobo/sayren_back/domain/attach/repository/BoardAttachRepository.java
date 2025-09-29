package com.imchobo.sayren_back.domain.attach.repository;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardAttachRepository extends JpaRepository<Attach, Long> {
  List<Attach> findByBoardId(Long boardId);
}
