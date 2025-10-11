package com.imchobo.sayren_back.domain.product.repository;

import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.zip.ZipFile;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
    List<ProductTag> findByProductId(Long productId);

}
