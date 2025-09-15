package com.imchobo.sayren_back.service;

import com.imchobo.sayren_back.entity.AttachCrawl;
import com.imchobo.sayren_back.entity.ProductCrawl;
import com.imchobo.sayren_back.entity.Tag;
import com.imchobo.sayren_back.repository.AttachCrawlRepository;
import com.imchobo.sayren_back.repository.ProductCrawlRepository;
import com.imchobo.sayren_back.repository.TagCrawlRepository;
import com.imchobo.sayren_back.util.CrolingUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CrawlService {
  // 이전에는 리스트 따로, 상세페이지 링크 따로해서 크롤링함.
  // 이제는 카테고리 리스트 들어갔다가, 상세로 알아서 넘어가는. 형식

  private final ProductCrawlRepository productCrawlRepository;
  private final AttachCrawlRepository attachCrawlRepository;
  private final CrolingUtil crolingUtil;
  private final TagCrawlRepository tagCrawlRepository;

// 밑에있는건 이전 상세페이지 크롤링하는 코드만
//  public void crawlAndSave(String url) throws Exception {
//    WebDriverManager.chromedriver().setup();
//    WebDriver driver = new ChromeDriver();
//    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//    driver.manage().window().maximize();
//
//    try {
//      driver.get(url);
//      Thread.sleep(2000);
//
//      // 1. Product 크롤링
//      String name = crolingUtil.getName(driver);
//      String description = crolingUtil.getDescription(driver);
//      int price = crolingUtil.getPrice(driver);
//      String category = crolingUtil.getCategory(driver);
//      String modelName = crolingUtil.getModelName(driver);
//
//      ProductCrawl productCrawl = ProductCrawl.builder()
//              .name(name)
//              .description(description)
//              .price(price)
//              .productCategory(category)
//              .modelName(modelName)
//              .build();
//
//      productCrawlRepository.save(productCrawl);
//
//      // 2. 썸네일 이미지 저장
//      String thumbnailPath = crolingUtil.getThumbnail(driver);
//      if (thumbnailPath != null) {
//        String path = thumbnailPath.substring(0, thumbnailPath.lastIndexOf("/"));
//        String uuid = thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1);
//
//        AttachCrawl attachCrawl = AttachCrawl.builder()
//                .uuid(uuid)
//                .path(path)
//                .isThumbnail(true)
//                .productCrawl(productCrawl)
//                .build();
//
//        attachCrawlRepository.save(attachCrawl);
//      }
//
//      // 3. 상세설명 이미지 저장
//      List<String> descImgs = crolingUtil.getDescriptionImageUrls(driver);
//
//      for (String fullPath : descImgs) {
//        String fullUrl = fullPath.substring(0, fullPath.lastIndexOf("/"));
//        String uuid = fullPath.substring(fullPath.lastIndexOf("/") + 1);
//
//
//        String[] parts = fullUrl.split(".amazonaws.com/");
//        String path = parts.length > 1 ? parts[1] : "";
//
//        AttachCrawl descAttach = AttachCrawl.builder()
//                .uuid(uuid)
//                .path(path)
//                .isThumbnail(false)   // 상세설명 이미지니까 false
//                .productCrawl(productCrawl)
//                .build();
//
//        attachCrawlRepository.save(descAttach);
//      }
//
//      // 4. 태그 저장
//      // 카테고리(리스트) 페이지에서 뽑아온 태그를 상세페이지 모델명과 매칭
//      Map<String, Map<String, String>> tags = CrolingUtil.getTag(driver);
//      Map<String, String> currentSpecs = tags.get(modelName);
//
//      if (currentSpecs != null && !currentSpecs.isEmpty()) {
//        currentSpecs.forEach((tagName, tagValue) -> {
//          // value가 여러 개면 쉼표 기준으로 분리 저장
//          if (tagValue.contains(",")) {
//            for (String v : tagValue.split(",")) {
//              tagRepository.save(
//                      Tag.builder()
//                              .product(productCrawl)
//                              .tagName(tagName)
//                              .tagValue(v.trim())
//                              .build()
//              );
//            }
//          } else {
//            tagRepository.save(
//                    Tag.builder()
//                            .product(productCrawl)
//                            .tagName(tagName)
//                            .tagValue(tagValue.trim())
//                            .build()
//            );
//          }
//        });
//      }
//      System.out.println("DB 저장 완료: " + productCrawl.getName());
//
//    } finally {
//      driver.quit();
//    }
//  }

  /**
   * 카테고리 페이지 크롤링 메서드
   * 1. 카테고리 URL 접속
   * 2. 제품 리스트 li 요소 탐색
   * 3. 각 li에서 상세페이지 링크 추출
   * 4. 상세페이지 크롤링 실행
   */
  public void crawlCategory(String categoryUrl) throws Exception {
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.manage().window().maximize();

    try {
      // 카테고리 페이지 열기
      driver.get(categoryUrl);
      Thread.sleep(2000);

      // 리스트에서 스펙 태그 수집 (제품 모델명 → 스펙 Map 구조)
      Map<String, Map<String, String>> tagMap = CrolingUtil.getTag(driver);

      // 제품 리스트 li 요소 전체 찾기
      List<WebElement> products = driver.findElements(By.cssSelector("ul[role=list] > li"));

      System.out.println("리스트 Count: " + products.size());

      // 카테고리 페이지에서 제품 리스트 크롤링 반복
      for (int i = 0; i < products.size(); i++) {
        try {
          // 1. 매번 카테고리 페이지로 돌아가기
          //    이유: 이전에 상세페이지로 이동했기 때문에
          //    다시 카테고리 페이지를 로딩해야 다음 상품 li를 찾을 수 있음
          driver.get(categoryUrl);
          Thread.sleep(2000); // 페이지 로딩 대기

          // 2. 카테고리 페이지에서 제품 리스트 다시 읽기
          //    driver.get()을 다시 했기 때문에 products 리스트도 새로 읽어와야 함
          products = driver.findElements(By.cssSelector("ul[role=list] > li"));

          // 3. i번째 제품 요소 선택
          //    products 리스트는 다시 읽었지만, 인덱스 i로 접근하면
          //    처음에 세어둔 products.size() 기준으로 모든 상품에 접근 가능
          WebElement product = products.get(i);

          // 4. 제품 li 안에서 상세페이지 링크(a 태그) 추출
          WebElement linkElement = product.findElement(By.xpath(".//a[@href]"));
          String href = linkElement.getAttribute("href");

          // 5. 상대경로일 경우 앞에 LG 도메인을 붙여서 절대경로로 변환
          String detailUrl = href.startsWith("http") ? href : "https://www.lge.co.kr" + href;

          System.out.println("detailUrl" + detailUrl);

          // 6. 상세페이지 크롤링 실행 (상품 정보 + 이미지 + 태그 저장)
          crawlAndSaveWithTags(driver, detailUrl, tagMap);

          System.out.println("6. 크롤링종료");

        } catch (Exception e) {
          // 상품 하나라도 실패하면 로그만 찍고 다음 상품으로 넘어감
          System.err.println("상품 크롤링 실패: " + e.getMessage());
          continue;
        }
      }

    } finally {
      driver.quit(); // 크롬 종료
    }
  }

  /**
   * 상세페이지 하나를 열고 상품 데이터를 DB에 저장하는 메서드
   * - ProductCrawl: 상품 기본 정보
   * - AttachCrawl: 썸네일, 상세 이미지
   * - Tag: 스펙 태그
   */
  private void crawlAndSaveWithTags(WebDriver driver, String url, Map<String, Map<String, String>> tagMap) throws Exception {
    try {
      // 상세페이지 접속
      System.out.println("crawlAndSaveWithTags: url: " + url);
      driver.get(url);
      Thread.sleep(2000);

      // 스크롤 끝까지 내려서 lazy-loading 이미지 로드
      JavascriptExecutor js = (JavascriptExecutor) driver;
      long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

      System.out.println("DEBUG 1");

      while (true) {
        System.out.println("6. 크롤링종료");
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(1000);
        long newHeight = (long) js.executeScript("return document.body.scrollHeight");
        if (newHeight == lastHeight) break; // 더 이상 변화 없으면 종료
        lastHeight = newHeight;
      }

      System.out.println("DEBUG 2");
      // 상품 기본 정보 가져오기
      String name = crolingUtil.getName(driver);
      System.out.println("DEBUG 2-1");
      String description = crolingUtil.getDescription(driver);
      System.out.println("DEBUG 2-2");
      int price = crolingUtil.getPrice(driver);
      System.out.println("DEBUG 2-3");
      String category = crolingUtil.getCategory(driver);
      System.out.println("DEBUG 2-4");
      String modelName = crolingUtil.getModelName(driver);

      System.out.println("DEBUG 3");
      // DB 중복 체크 (modelName이 이미 있으면 저장하지 않음)
      Optional<ProductCrawl> existing = productCrawlRepository.findByModelName(modelName);
      if (existing.isPresent()) {
        System.out.println("이미 존재하는 상품: " + modelName);
        return;
      }

      System.out.println("DEBUG 4");
      // 상품 저장
      ProductCrawl productCrawl = ProductCrawl.builder()
              .name(name)
              .description(description)
              .price(price)
              .productCategory(category)
              .modelName(modelName)
              .build();
      productCrawlRepository.save(productCrawl);

      System.out.println("DEBUG 5");
      // 썸네일 이미지 저장
      String thumbnailPath = crolingUtil.getThumbnail(driver);
      System.out.println("thumbnailPath : " + thumbnailPath);
      if (thumbnailPath != null) {
        String path = thumbnailPath.substring(0, thumbnailPath.lastIndexOf("/"));
        String uuid = thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1);

        AttachCrawl attachCrawl = AttachCrawl.builder()
                .uuid(uuid)
                .path(path)
                .isThumbnail(true)
                .productCrawl(productCrawl)
                .build();

        attachCrawlRepository.save(attachCrawl);
      }

      System.out.println("DEBUG 6");
      // 상세설명 이미지 저장
      List<String> descImgs = crolingUtil.getDescriptionImageUrls(driver);
      System.out.println("descImgs : " + descImgs.get(0));
      for (String fullPath : descImgs) {
        String fullUrl = fullPath.substring(0, fullPath.lastIndexOf("/"));
        String uuid = fullPath.substring(fullPath.lastIndexOf("/") + 1);

        String[] parts = fullUrl.split(".amazonaws.com/");
        String path = parts.length > 1 ? parts[1] : "";

        AttachCrawl descAttach = AttachCrawl.builder()
                .uuid(uuid)
                .path(path)
                .isThumbnail(false)
                .productCrawl(productCrawl)
                .build();

        attachCrawlRepository.save(descAttach);
      }

      System.out.println("DEBUG 7");
      // 태그 저장 (리스트에서 수집한 Map 기반)
      Map<String, String> currentSpecs = tagMap.get(modelName);
      if (currentSpecs != null && !currentSpecs.isEmpty()) {
        currentSpecs.forEach((tagName, tagValue) -> {
          if (tagValue.contains(",")) {
            // 값이 여러 개일 경우 쉼표 기준으로 분리 저장
            for (String v : tagValue.split(",")) {
              tagCrawlRepository.save(
                      Tag.builder()
                              .product(productCrawl)
                              .tagName(tagName)
                              .tagValue(v.trim())
                              .build()
              );
            }
          } else {
            // 단일 값은 그대로 저장
            tagCrawlRepository.save(
                    Tag.builder()
                            .product(productCrawl)
                            .tagName(tagName)
                            .tagValue(tagValue.trim())
                            .build()
            );
          }
        });
      }

      System.out.println("DB 저장 완료: " + productCrawl.getName());

    } catch (Exception e) {
      System.err.println("상세 크롤링 실패 (" + url + "): " + e.getMessage());
    }
  }
}