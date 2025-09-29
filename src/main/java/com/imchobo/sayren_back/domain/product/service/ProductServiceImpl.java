package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.attach.dto.ProductAttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.repository.ProductAttachRepository;
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
  private final ProductAttachRepository productAttachRepository;

  @Override
  @EventListener(ApplicationReadyEvent.class)
  public void preloadProducts() {
    List<ProductListResponseDTO> list = getAllProducts();
    redisUtil.setObject("PRODUCTS", list);
  }


  @Override
  public List<ProductListResponseDTO> getAllProducts() {
    return productRepository.findAll().stream()
            .map(p -> ProductListResponseDTO.builder()
                    .productId(p.getId())
                    .thumbnailUrl(
                            productAttachRepository.findByProductIdAndIsThumbnailTrue(p.getId())
                            .map(a -> "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                    + a.getPath() + "/" + a.getUuid())
                            .orElse(null)) // attach 연결
                    .productName(p.getName())
                    .description(
                            p.getDescription() != null ? p.getDescription() : "" )
                    .price(p.getPrice())
                    .isUse(p.getIsUse())
                    .productCategory(p.getProductCategory())
                    .modelName(p.getModelName())
                    // 태그
                    .tags(
                    productTagRepository.findByProductId(p.getId()).stream()
                            .map(ProductTag::getTagValue)
                            .toList())
                    .build()
            )
            .toList();
  }

  @Override
  public ProductDetailsResponseDTO getProductById(Long id) {
    return productRepository.findById(id)
            .map(p -> new ProductDetailsResponseDTO(
                    p.getId(),
                    p.getName(),
                    p.getDescription() != null ? p.getDescription() : "",
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
                    // attach
                    productAttachRepository.findByProductId(p.getId()).stream()
                            .map(a -> ProductAttachResponseDTO.builder()
                                    .attachId(a.getId())
                                    .url("https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                            + a.getPath() + "/" + a.getUuid())
                                    .build()
                            )
                            .toList()
            ))
            .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
  }
}
