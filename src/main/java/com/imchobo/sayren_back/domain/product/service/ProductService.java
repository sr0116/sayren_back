package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.product.dto.PurchaseProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.PurchaseProductListResponseDTO;

import java.util.List;

public interface ProductService {
  void preloadProducts();
  List<PurchaseProductListResponseDTO> getAllProducts();
  PurchaseProductDetailsResponseDTO getProductById(Long id);
}
