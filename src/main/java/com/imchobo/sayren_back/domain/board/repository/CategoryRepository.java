package com.imchobo.sayren_back.domain.board.repository;

import com.imchobo.sayren_back.domain.board.en.CategoryType;
import com.imchobo.sayren_back.domain.board.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  // 카테고리 타입으로 조회
  Optional<Category> findByType(CategoryType type);


  // parentCategory의 type이 PRODUCT이고, name이 일치하는 카테고리 찾기
  Optional<Category> findByNameAndParentCategory_Type(String name, CategoryType type);


  // 특정 타입(Product) 하위 카테고리 전체 조회
  List<Category> findByParentCategory_Type(CategoryType type);

  List<Category> findByTypeAndParentCategory_Id(CategoryType type, Long parentCategoryId);
}
