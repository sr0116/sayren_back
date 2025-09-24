package com.imchobo.sayren_back.domain.product.repository;

import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
}
