package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.attach.dto.ProductAttachResponseDTO;
import com.imchobo.sayren_back.domain.attach.repository.ProductAttachRepository;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.product.dto.ProductDetailsResponseDTO;
import com.imchobo.sayren_back.domain.product.dto.ProductListResponseDTO;
import com.imchobo.sayren_back.domain.product.entity.Product;
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
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final RedisUtil redisUtil;
  private final ProductStockRepository productStockRepository;
  private final ProductTagRepository productTagRepository;
  private final ProductAttachRepository productAttachRepository;

  private Long calcDeposit(Long price) {
    // 보증금: 원가의 20%
    return Math.round(price * 0.2);
  }

  private Long calcRentalPrice(Long price, Integer month) {
    if (month == null || month == 0) return price;

    // 원가에서 보증금을 뺀 금액을 개월 수로 나눔
    long deposit = calcDeposit(price);
    long base = Math.round((price - deposit) / (double) month);

    // 개월 수에 따른 장기 계약 혜택 적용
    if (month == 36) {
      return base - 500;   // 36개월 계약 시 월 500원 할인
    } else if (month == 48) {
      return base - 1000;  // 48개월 계약 시 월 1000원 할인
    }

    return base; // 24개월은 그대로
  }


  @Override
  @EventListener(ApplicationReadyEvent.class)
  public void preloadProducts() {
    List<ProductListResponseDTO> list = getAllProducts(null, null);
    redisUtil.setObject("PRODUCTS", list);
  }


  @Override
  public List<ProductListResponseDTO> getAllProducts(String type, String category) {
    List<Product> products;

    if (category != null && !category.isEmpty()) {
      if (type != null && !type.isEmpty()) {
        products = productRepository.findByCategoryAndType(
                category,
                OrderPlanType.valueOf(type.toUpperCase())
        );
      } else {
        products = productRepository.findByProductCategory(category);
      }
    } else {
      if (type != null && !type.isEmpty()) {
        products = productRepository.findByOrderPlanType(
                OrderPlanType.valueOf(type.toUpperCase())
        );
      } else {
        products = productRepository.findAll();
      }
    }

    return products.stream()
            .map(p -> ProductListResponseDTO.builder()
                    .productId(p.getId())
                    .thumbnailUrl(
                            productAttachRepository.findByProductIdAndIsThumbnailTrue(p.getId())
                                    .map(a -> "https://kiylab-bucket.s3.ap-northeast-2.amazonaws.com/"
                                            + a.getPath() + "/" + a.getUuid())
                                    .orElse(null))
                    .productName(p.getName())
                    .description(p.getDescription() != null ? p.getDescription() : "")
                    .price(p.getPrice())
                    .isUse(p.getIsUse())
                    .productCategory(p.getProductCategory())
                    .modelName(p.getModelName())
                    .tags(productTagRepository.findByProductId(p.getId()).stream()
                            .map(ProductTag::getTagValue)
                            .toList())
                    .deposit(calcDeposit(p.getPrice()))
                    .rentalPrice(calcRentalPrice(p.getPrice(), 24))
                    .build())
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
                            .toList(),
                    // order
                    p.getOrderItems().stream()
                            .map(item -> item.getOrderPlan().getType().name())
                            .distinct()
                            .toList(),
                    calcDeposit(p.getPrice()),
                    calcRentalPrice(p.getPrice(), 24)
            ))
            .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
  }
}
