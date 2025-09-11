package com.kiylab.croling.repository;

import com.kiylab.croling.entity.AttachCrawl;
import com.kiylab.croling.entity.ProductCrawl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Commit
public class AttachCrawlRepositoryTest {

  @Autowired
  private ProductCrawlRepository productRepository;
  @Autowired
  private AttachCrawlRepository attachRepository;

  @Test
  void testInsertAttach() {
    ProductCrawl productCrawl = productRepository.save(
            ProductCrawl.builder()
                    .name("테스트상품")
                    .description("설명입니다")
                    .price(20000)
                    .productCategory("가전")
                    .modelName("TEST-200")
                    .build()
    );

    AttachCrawl attach = AttachCrawl.builder()
            .uuid("abc-123.webp")
            .path("2025/09/09")
            .isThumbnail(true)
            .productCrawl(productCrawl)
            .build();

    attachRepository.save(attach);
  }
}
