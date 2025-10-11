package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductPendingDTO;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;

import java.util.List;

public interface ProductService {
  void preloadProducts();
  List<ProductListResponseDTO> getAllProducts(String type, String category);
  ProductDetailsResponseDTO getProductById(Long id);

  void useProduct(Long id);

  List<ProductPendingDTO> getPendingProducts();

}

