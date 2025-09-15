package com.imchobo.sayren_back.repository;

import com.imchobo.sayren_back.entity.ProductCrawl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCrawlRepository extends JpaRepository<ProductCrawl, Long> {

  Optional<ProductCrawl> findByModelName(String modelName);
}

