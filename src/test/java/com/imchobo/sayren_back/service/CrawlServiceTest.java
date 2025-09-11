package com.imchobo.sayren_back.service;

import com.imchobo.sayren_back.repository.ProductCrawlRepository;
import com.imchobo.sayren_back.repository.TagCrawlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@Transactional
//@Rollback(false)
public class CrawlServiceTest {

  @Autowired
  private CrawlService crawlService;

  @Autowired
  private ProductCrawlRepository productCrawlRepository;

  @Autowired
  private TagCrawlRepository tagRepository;


  @Test
    public void crawlingTest() throws Exception {
      crawlService.crawlCategory("https://www.lge.co.kr/category/humidifiers");

      //https://www.lge.co.kr/water-purifiers/wd323acb
    }

//  @Test
//  void crawlingAndSaveTest() throws Exception {
//    // 상세페이지 URL (상품 1개)
//    String url = "https://www.lge.co.kr/water-purifiers";
//
//    // 크롤링 실행 (Product + Tag 저장)
//    crawlService.crawlAndSave(url);
//
//    // 저장된 상품 확인
//    List<ProductCrawl> products = productCrawlRepository.findAll();
//    System.out.println("===== 저장된 상품 =====");
//    products.forEach(p -> System.out.println(p.getProductId() + " | " + p.getModelName() + " | " + p.getName()));
//
//    // 저장된 태그 확인
//    List<Tag> tags = tagRepository.findAll();
//    System.out.println("===== 저장된 태그 =====");
//    tags.forEach(t -> System.out.println(
//            "productId=" + t.getProduct().getProductId() +
//                    " | tagName=" + t.getTagName() +
//                    " | tagValue=" + t.getTagValue()
//    ));
//  }
}
