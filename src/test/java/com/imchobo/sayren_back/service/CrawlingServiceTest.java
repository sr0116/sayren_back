package com.imchobo.sayren_back.service;

import com.imchobo.sayren_back.domain.crawling.service.CrawlingService;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CrawlingServiceTest {

  @Autowired
  private CrawlingService crawlingService;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductTagRepository productTagRepository;


  @Test
  public void crawlingTest() throws Exception {
    crawlingService.crawlCategory("https://www.lge.co.kr/category/dehumidifiers");

  }
}
