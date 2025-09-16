package com.imchobo.sayren_back.domain.crawling.service;

import com.imchobo.sayren_back.domain.attach.entity.Attach;
import com.imchobo.sayren_back.domain.attach.repository.AttachRepository;
import com.imchobo.sayren_back.domain.crawling.util.CrawlingUtil;
import com.imchobo.sayren_back.domain.product.entity.Product;
import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductTagRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.Tag;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional
public class CrawlingService {
  // 이전에는 리스트 따로, 상세페이지 링크 따로해서 크롤링함.
  // 이제는 카테고리 리스트 들어갔다가, 상세로 알아서 넘어가는. 형식

  private final ProductRepository productRepository;
  private final AttachRepository attachRepository;
  private final CrawlingUtil crawlingUtil;
  private final ProductTagRepository productTagRepository;
//  private final ProductOptionRepository productOptionRepository;


  /**
   * 카테고리 페이지 크롤링
   * - 카테고리 페이지 접속
   * - 제품 리스트 li 요소 탐색
   * - 각 제품의 상세페이지 링크를 얻어 crawlAndSaveWithTags() 호출
   */
  public void crawlCategory(String categoryUrl) throws Exception {
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.manage().window().maximize();

    try {
      driver.get(categoryUrl);
      Thread.sleep(2000);

      // 리스트에서 태그 맵(모델명 → 스펙 Map) 추출
      Map<String, Map<String, String>> tagMap = CrawlingUtil.getTag(driver);

      // 제품 리스트 li
      List<WebElement> products = driver.findElements(By.cssSelector("ul[role=list] > li"));
      System.out.println("리스트 Count: " + products.size());

      for (int i = 0; i < products.size(); i++) {
        try {
          // 다시 카테고리 페이지로 이동 (상세페이지 다녀오면 products가 깨지므로 재로딩)
          driver.get(categoryUrl);
          Thread.sleep(2000);

          // 새로 로딩된 products 리스트 가져오기
          products = driver.findElements(By.cssSelector("ul[role=list] > li"));
          WebElement product = products.get(i);

          // 상세페이지 링크 추출
          WebElement linkElement = product.findElement(By.xpath(".//a[@href]"));
          String href = linkElement.getAttribute("href");
          String detailUrl = href.startsWith("http") ? href : "https://www.lge.co.kr" + href;

          System.out.println("detailUrl" + detailUrl);

          // 상세페이지 크롤링 실행
          crawlAndSaveWithTags(driver, detailUrl, tagMap);

          System.out.println("크롤링 완료");
        } catch (Exception e) {
          // 상품 하나 크롤링 실패해도 다음 상품으로 넘어감
          System.err.println("상품 크롤링 실패: " + e.getMessage());
          continue;
        }
      }

    } finally {
      driver.quit();
    }
  }

  /**
   * 상세페이지 크롤링
   * - ProductCrawl 저장
   * - AttachCrawl(썸네일/상세이미지) 저장
   * - Tag 저장
   * - ProductOption(구매/구독) 저장
   */
  private void crawlAndSaveWithTags(WebDriver driver, String url, Map<String, Map<String, String>> tagMap) throws Exception {
    try {
      System.out.println("crawlAndSaveWithTags: url: " + url);
      driver.get(url);
      Thread.sleep(2000);

      // lazy-loading 이미지 로딩을 위해 스크롤 끝까지 내리기
      JavascriptExecutor js = (JavascriptExecutor) driver;
      long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

      while (true) {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(1000);
        long newHeight = (long) js.executeScript("return document.body.scrollHeight");
        if (newHeight == lastHeight) break;
        lastHeight = newHeight;
      }

      // 상품 기본 정보 수집
      String name = crawlingUtil.getName(driver);
      String description = crawlingUtil.getDescription(driver);
      int price = url.contains("care-solutions") ?
              crawlingUtil.getSubscribePrice(driver) : // 구독 상품이면 월 구독 가격
              crawlingUtil.getPrice(driver);           // 구매 상품이면 정가
      String category = crawlingUtil.getCategory(driver);
      String modelName = crawlingUtil.getModelName(driver);

      // DB 중복 체크 (modelName 기준)
      Optional<Product> existing = productRepository.findByModelName(modelName);
      Product product;

      if (existing.isPresent()) {
        // 이미 DB에 존재 → 해당 ProductCrawl 재사용
        System.out.println("이미 존재하는 상품: " + modelName);
        product = existing.get();
      } else {
        // 신규 상품 저장
        product = productRepository.save(Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .productCategory(category)
                .modelName(modelName)
                .isUse(false)
                .build());
      }

      // 썸네일 이미지 저장 (null, 공백 방지 + 예외 처리)
      String thumbnailPath = crawlingUtil.getThumbnail(driver);
      if (thumbnailPath != null && !thumbnailPath.isBlank()) {
        try {
          String path = thumbnailPath.substring(0, thumbnailPath.lastIndexOf("/"));
          String uuid = thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1);

          Attach attach = Attach.builder()
                  .uuid(uuid)
                  .path(path)
                  .isThumbnail(true)
                  .product(product)
                  .build();

          attachRepository.save(attach);
        } catch (Exception e) {
          System.err.println("썸네일 저장 실패: " + e.getMessage());
        }
      }

      // 상세 이미지 저장 (null, 빈 리스트 방지 + 예외 처리)
      List<String> descImgs = crawlingUtil.getDescriptionImageUrls(driver);
      if (descImgs != null && !descImgs.isEmpty()) {
        for (String fullPath : descImgs) {
          try {
            String fullUrl = fullPath.substring(0, fullPath.lastIndexOf("/"));
            String uuid = fullPath.substring(fullPath.lastIndexOf("/") + 1);

            String[] parts = fullUrl.split(".amazonaws.com/");
            String path = parts.length > 1 ? parts[1] : "";

            Attach descAttach = Attach.builder()
                    .uuid(uuid)
                    .path(path)
                    .isThumbnail(false)
                    .product(product)
                    .build();

            attachRepository.save(descAttach);
          } catch (Exception e) {
            System.err.println("상세 이미지 저장 실패: " + e.getMessage());
          }
        }
      }

      // 태그 저장 (리스트 페이지에서 긁은 tagMap 기반)
      Map<String, String> currentSpecs = tagMap.get(modelName);
      if (currentSpecs != null && !currentSpecs.isEmpty()) {
        currentSpecs.forEach((tagName, tagValue) -> {
          try {
            if (tagValue.contains(",")) {
              for (String v : tagValue.split(",")) {
                productTagRepository.save(ProductTag.builder()
                        .product(product)
                        .tagName(tagName)
                        .tagValue(v.trim())
                        .build());
              }
            } else {
              productTagRepository.save(ProductTag.builder()
                      .product(product)
                      .tagName(tagName)
                      .tagValue(tagValue.trim())
                      .build());
            }
          } catch (Exception e) {
            System.err.println("태그 저장 실패: " + tagName + " - " + e.getMessage());
          }
        });
      }

      System.out.println("DB 저장 완료: " + product.getName());

    } catch (Exception e) {
      System.err.println("상세 크롤링 실패 (" + url + "): " + e.getMessage());
    }
  }
}
