package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;

import java.util.List;

public interface ProductService {
  void preloadProducts();
  List<ProductListResponseDTO> getAllProducts(String type);
  ProductDetailsResponseDTO getProductById(Long id);


}

