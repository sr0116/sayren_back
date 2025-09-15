package com.kiylab.croling.service;

import com.kiylab.croling.entity.ProductCrawl;
import com.kiylab.croling.entity.Tag;
import com.kiylab.croling.repository.ProductCrawlRepository;
import com.kiylab.croling.repository.TagRepository;
import com.kiylab.croling.util.CrolingUtil;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootTest
//@Transactional
//@Rollback(false)
public class CrawlServiceTest {

  @Autowired
  private CrawlService crawlService;

  @Autowired
  private ProductCrawlRepository productCrawlRepository;

  @Autowired
  private TagRepository tagRepository;


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
