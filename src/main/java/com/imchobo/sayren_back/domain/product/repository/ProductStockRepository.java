package com.imchobo.sayren_back.domain.product.repository;

import com.imchobo.sayren_back.domain.product.entity.ProductStock;
import io.lettuce.core.Value;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    Optional<ProductStock> findByProductId(Long productId);

}
