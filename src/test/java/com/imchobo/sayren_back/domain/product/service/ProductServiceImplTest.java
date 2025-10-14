package com.imchobo.sayren_back.domain.product.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceImplTest {
  @Autowired
  private ProductService productService;

  @Test
  void revalidate() {
    productService.revalidate(72L);
  }

  @Test
  void revalidateAll() {
    productService.revalidateAll();
  }
}