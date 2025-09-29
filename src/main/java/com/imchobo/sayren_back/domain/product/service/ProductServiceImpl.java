package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.attach.dto.BoardAttachResponseDTO;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.entity.ProductStock;
import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductStockRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
  private final ProductRepository productRepository;
  private final RedisUtil redisUtil;
  private final ProductStockRepository productStockRepository;
  private final ProductTagRepository productTagRepository;

  @Override
  @EventListener(ApplicationReadyEvent.class)
  public void preloadProducts() {
    redisUtil.setObject("PRODUCTS", productRepository.findAll());
  }


  @Override
  public List<ProductListResponseDTO> getAllProducts() {
    return productRepository.findAll().stream()
            .map(p -> new ProductListResponseDTO(
                    p.getId(),
                    null, // thumbnailUrl: attach 연결 후 채워야 함
                    p.getName(),
                    p.getPrice().intValue(),
                    p.getIsUse()
            ))
            .toList();
  }

  @Override
  public ProductDetailsResponseDTO getProductById(Long id) {
    return productRepository.findById(id)
            .map(p -> new ProductDetailsResponseDTO(
                    p.getId(),
                    p.getName(),
                    p.getDescription() != null
                            ? p.getDescription().replaceAll("<[^>]*>", "").trim()
                            : "",
                    p.getPrice().intValue(),
                    p.getIsUse(),
                    p.getProductCategory(),
                    p.getModelName(),
                    p.getRegDate(),
                    // stock
                    productStockRepository.findByProductId(p.getId())
                            .map(ProductStock::getStock).orElse(0),
                    // 태그
                    productTagRepository.findByProductId(p.getId()).stream()
                            .map(ProductTag::getTagValue)
                            .toList(),
                    null // attachList 자리 → 지금은 안씀
            ))
            .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
  }
}
