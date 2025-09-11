package com.kiylab.croling.repository;

import com.kiylab.croling.entity.ProductCrawl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Commit
public class ProductCrawlRepositoryTest {

  @Autowired
  private ProductCrawlRepository productRepository;

  @Test
  void testInsert() {
    ProductCrawl product = ProductCrawl.builder()
            .name("테스트상품")
            .description("설명입니다")
            .price(10000)
            .productCategory("가전")
            .modelName("TEST-100")
            .build();

    productRepository.save(product);
  }
}
