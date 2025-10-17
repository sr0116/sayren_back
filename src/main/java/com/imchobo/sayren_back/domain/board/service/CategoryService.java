package com.imchobo.sayren_back.domain.board.service;

import com.imchobo.sayren_back.domain.board.entity.Category;
import com.imchobo.sayren_back.domain.board.repository.CategoryRepository;

import java.util.List;

public interface CategoryService {

    // type=PRODUCT, parent_category_id=1 조건으로 카테고리 조회
    List<Category> findProductCategories();
}
