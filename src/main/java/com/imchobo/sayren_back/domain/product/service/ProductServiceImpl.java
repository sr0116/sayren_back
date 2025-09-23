package com.imchobo.sayren_back.domain.product.service;

import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
  private final ProductRepository productRepository;
  private final RedisUtil redisUtil;
  @Override
  @EventListener(ApplicationReadyEvent.class)
  public void preloadProducts() {
    redisUtil.setObject("PRODUCTS", productRepository.findAll());
  }
}
