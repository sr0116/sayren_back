package com.imchobo.sayren_back.domain.board.repository;

import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  // 카테고리 타입으로 조회
  Optional<Category> findByType(CategoryType type);
}
