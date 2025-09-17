package com.imchobo.sayren_back.domain.product.repository;

import com.imchobo.sayren_back.domain.product.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
}
