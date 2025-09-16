package com.imchobo.sayren_back.domain.crawling.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductTagRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
@Getter
public class CrawlingUtil {

  private final S3UploadUtil s3UploadUtil;
  private final ProductRepository productRepository;
  private final ProductTagRepository productTagRepository;

  public void printPageHtml(String url) throws Exception {
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.manage().window().maximize();

    try {
      driver.get(url);
      Thread.sleep(2000);

//      String name = getName(driver);
//      String description = getDescription(driver);
//      int price = getPrice(driver);
//      String category = getCategory(driver);
//      String thumbnail = getThumbnail(driver);
//      String modelName = getModelName(driver);
      Map<String, Map<String, String>> tags = getTag(driver);


      // 최종 결과 출력
      System.out.println("===== data-* 속성 제거된 첫 번째 iw_placeholder 블록 =====");
      System.out.println(tags);

      tags.forEach((listkey, listval) -> {
        if(productRepository.findByModelName(listkey).isPresent()){
          if (listval.size() > 0){
            listval.forEach((listkey2, listval2) -> {
              if(listval2.split(",").length > 1){
                for(String t : listval2.split(",")){
                  productTagRepository.save(ProductTag.builder().product(productRepository.findByModelName(listkey).get()).tagName(listkey2).tagValue(t).build());
                }
              }
              else {

                productTagRepository.save(ProductTag.builder().product(productRepository.findByModelName(listkey).get()).tagName(listkey2).tagValue(listval2).build());
              }
            });
          }
        }
      } );

    } finally {
      driver.quit();
    }
  }

  private static void cleanDataAttributes(WebDriver driver, WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) driver;

    js.executeScript(
            "var el = arguments[0]; " +
                    "var attrs = el.attributes; " +
                    "for (var i = attrs.length - 1; i >= 0; i--) { " +
                    "  if (attrs[i].name.startsWith('data-')) { " +
                    "    el.removeAttribute(attrs[i].name); " +
                    "  } " +
                    "}", element
    );
  }

  public String getName(WebDriver driver) {
    try {
      WebElement nameEl = driver.findElement(By.cssSelector("h2.name"));
      String fullText = nameEl.getText();
      String name = fullText.split("\n")[0].trim();
      return replaceBrandName(name); // 치환 적용
    } catch (Exception e) {
      return "상품명 없음";
    }
  }

  public String getDescription(WebDriver driver) throws Exception {
    WebElement firstBlock = driver.findElement(By.className("iw_placeholder"));

    List<WebElement> allElements = firstBlock.findElements(By.xpath(".//*"));
    for (WebElement el : allElements) {
      cleanDataAttributes(driver, el);
    }

    String html = firstBlock.getDomProperty("outerHTML");
    org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);

    // 불필요한 wrapper 요소 제거
    doc.select(".banner-wrap, .img-wrap, .img-alt, .mo, .mo-only, button, .bullet-list, .animation-area").remove();

    // a 태그 제거 (안에 있는 텍스트나 이미지만 살려서 대체)
    for (org.jsoup.nodes.Element a : doc.select("a")) {
      a.unwrap(); // <a>만 제거, 내부 내용은 유지
    }


    // 1. <img src="..."> 처리 + alt 분리
    for (org.jsoup.nodes.Element img : doc.select("img")) {
      String src = img.attr("src");

      // src가 없으면 data-*에서 대체 (이미 data는 지웠을 가능성 있으니 보강용)
      if (src == null || src.isBlank()) continue;

      if (!src.startsWith("http")) {
        src = "https://www.lge.co.kr" + src;
      }
      String newSrc = src.replace(" ", "%20");
      System.out.println("img src: ==" + src + "==");
      System.out.println("img newSrc: ==" + newSrc + "==");

      String s3Url = s3UploadUtil.getFullUrl(s3UploadUtil.upload(newSrc));

      // alt 텍스트 분리
      String altText = img.hasAttr("alt") ? img.attr("alt") : null;

      // 새로운 img 태그
      org.jsoup.nodes.Element newImg = new org.jsoup.nodes.Element("img")
              .attr("src", s3Url)
              .attr("style", "width:100%; height:auto; display:block;");

      // alt 텍스트는 <p>로 별도 생성
      if (altText != null && !altText.isBlank()) {
        org.jsoup.nodes.Element altPara = new org.jsoup.nodes.Element("p")
                .attr("class", "img-alt")
                .text(altText);

        // img + alt를 감싸는 wrapper div
        org.jsoup.nodes.Element wrapper = new org.jsoup.nodes.Element("div")
                .appendChild(newImg)
                .appendChild(altPara);

        img.replaceWith(wrapper);
      } else {
        img.replaceWith(newImg);
      }
    }

    // 2. style="background:url(...)" 처리 → img + alt 분리
    for (org.jsoup.nodes.Element el : doc.select("[style]")) {
      String style = el.attr("style");

      // 모바일 전용 요소 제거
      if (el.hasClass("mo") || el.hasClass("mo-only")) {
        el.remove();
        continue;
      }

      // 불필요한 클래스 제거
      if (el.hasClass("banner-wrap") || el.hasClass("img-wrap") || el.hasClass("img-alt")) {
        el.remove();
        continue;
      }

      if (style != null && style.contains("url(")) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("url\\(([^)]*)\\)")
                .matcher(style);

        if (matcher.find()) {
          String url = matcher.group(1)
                  .replace("\"", "")
                  .replace("'", "")
                  .trim();

          if (!url.startsWith("http")) {
            url = "https://www.lge.co.kr" + url;
          }

          String s3Url = s3UploadUtil.getFullUrl(s3UploadUtil.upload(url));

          // 새 img 태그 생성
          org.jsoup.nodes.Element newImg = new org.jsoup.nodes.Element("img")
                  .attr("src", s3Url)
                  .attr("style", "width:100%; height:auto; display:block;");

          // alt 속성 있으면 분리
          String altText = el.hasAttr("alt") ? el.attr("alt") : null;
          if (altText != null && !altText.isBlank()) {
            org.jsoup.nodes.Element altPara = new org.jsoup.nodes.Element("p")
                    .attr("class", "img-alt")
                    .text(altText);

            org.jsoup.nodes.Element wrapper = new org.jsoup.nodes.Element("div")
                    .appendChild(newImg)
                    .appendChild(altPara);

            el.replaceWith(wrapper);
          } else {
            el.replaceWith(newImg);
          }
        }
      }
    }

// src 없는 <img> 제거
    for (org.jsoup.nodes.Element img : doc.select("img")) {
      String src = img.attr("src");
      if (src == null || src.isBlank()) {
        img.remove();
      }
    }


    String resultHtml = doc.body().html();
    return replaceBrandName(resultHtml); // 치환 적용
  }

  public List<String> getDescriptionImageUrls(WebDriver driver) throws Exception {
    WebElement firstBlock = driver.findElement(By.className("iw_placeholder"));
    String html = firstBlock.getDomProperty("outerHTML");

    org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);

    List<String> imageUrls = new ArrayList<>();

    for (org.jsoup.nodes.Element img : doc.select("img")) {
      String src = img.attr("src");
      if (src != null && !src.isBlank()) {
        if (!src.startsWith("http")) {
          src = "https://www.lge.co.kr" + src;
        }
        String newSrc = src.replace(" ", "%20");

        String s3Url = s3UploadUtil.getFullUrl(s3UploadUtil.upload(newSrc));
        imageUrls.add(s3Url);
      }
    }
    return imageUrls;
  }

  public int getPrice(WebDriver driver) {
    // 가격이 나올 수 있는 후보 셀렉터들
    String[] selectors = {
            "dl.price-info.total-payment span.price em", // 결제 금액
            ".rental-price strong",                      // 렌탈가
            ".product-price strong",                     // 일반가
            ".price-area em"                             // 기타 케이스
    };

    // 1. 여러 셀렉터 순회
    for (String sel : selectors) {
      try {
        WebElement priceEl = driver.findElement(By.cssSelector(sel));
        String text = priceEl.getText().replaceAll("[^0-9]", "");
        if (!text.isBlank()) {
          return Integer.parseInt(text);
        }
      } catch (NoSuchElementException ignore) {
        // 해당 셀렉터 없으면 다음 시도
      }
    }

    // 2. JSON 기반 백업 (data-ec-product 속성 확인)
    try {
      WebElement jsonEl = driver.findElement(By.cssSelector("li[data-ec-product]"));
      String rawJson = jsonEl.getAttribute("data-ec-product");
      if (rawJson != null && !rawJson.isBlank()) {
        JsonNode node = new ObjectMapper().readTree(rawJson);
        if (node.has("price")) {
          return node.get("price").asInt();
        }
        if (node.has("rental_price")) { // 렌탈가 필드가 따로 있을 수도 있음
          return node.get("rental_price").asInt();
        }
      }
    } catch (Exception ignore) {
      // JSON도 없으면 무시
    }

    // 3. 끝까지 못 찾으면
    System.out.println("가격 없음");
    return -1;
  }

  public String getCategory(WebDriver driver) {
    try {
      List<WebElement> items = driver.findElements(
              By.cssSelector("ul[itemtype='http://schema.org/BreadcrumbList'] li"));

      if (items.size() < 2) {
        return "카테고리 없음";
      }

      WebElement target = items.get(items.size() - 2);
      WebElement textEl;
      try {
        textEl = target.findElement(By.tagName("a"));
      } catch (NoSuchElementException e) {
        textEl = target.findElement(By.tagName("span"));
      }

      return replaceBrandName(textEl.getText().trim()); // 치환 적용
    } catch (Exception e) {
      return "카테고리 없음";
    }
  }

  public String getThumbnail(WebDriver driver) {
    try {
      WebElement img = driver.findElement(By.cssSelector("img#base_detail_target"));
      String baseUrl = "https://www.lge.co.kr/";
      String imgUrl = img.getDomAttribute("src");
      return s3UploadUtil.upload(baseUrl + imgUrl);
    } catch (Exception e) {
      return null;
    }
  }

  public String getModelName(WebDriver driver) {
    try {
      WebElement button = driver.findElement(By.cssSelector("button.sku.copy"));
      String fullText = button.getText().trim();
      WebElement span = button.findElement(By.cssSelector("span.blind"));
      String blindText = span.getText().trim();
      String model = fullText.replace(blindText, "").trim();
      return replaceBrandName(model); // 치환 적용
    } catch (Exception e) {
      return "모델명 없음";
    }
  }

  // LG → SAYREN 치환
  private String replaceBrandName(String text) {
    if (text == null) return null;
    return text.replaceAll("(?i)lg", "SAYREN");
  }

  public static Map<String, Map<String, String>> getTag(WebDriver driver) {
    Map<String, Map<String, String>> productSpecMap = new LinkedHashMap<>();

    // ul[role=list] > li 전체 찾기
    List<WebElement> liElements = driver.findElements(
            By.cssSelector("ul[role=list] > li.CommonPcListUnitProduct_unit__h6bSJ")
    );

    for (WebElement li : liElements) {
      // 1. 모델명 추출
      String modelName = "";
      try {
        WebElement modelElement = li.findElement(By.cssSelector(".product-card-title_sku"));
        modelName = modelElement.getText().replace("모델명", "").trim(); // "모델명" 제거
      } catch (Exception e) {
        continue;
      }

      // 2. 내부 스펙 추출 (p 태그의 텍스트 = key, em = value)
      Map<String, String> specs = new LinkedHashMap<>();
      List<WebElement> pElements = li.findElements(
              By.cssSelector("div[class*='CommonPcListUnitProduct_spec_wrap'] p")
      );

      for (WebElement p : pElements) {
        try {
          String key = p.getText().replace(p.findElement(By.tagName("em")).getText(), "").trim();
          String value = p.findElement(By.tagName("em")).getText().trim();
          specs.put(key, value);
        } catch (Exception ignore) {}
      }

      productSpecMap.put(modelName, specs);
    }

    return productSpecMap;
  }

  /**
   * 구독 상품 가격 가져오기
   * - 화면에 표시된 월 요금 (.price.is-all-select)
   * - data-rental-sale-price 속성 (백업)
   */
  public int getSubscribePrice(WebDriver driver) {
    // 1. UI 표시된 월 요금
    try {
      WebElement priceEl = driver.findElement(By.cssSelector(".price.is-all-select"));
      String text = priceEl.getText().replaceAll("[^0-9]", "");
      if (!text.isBlank()) {
        return Integer.parseInt(text);
      }
    } catch (Exception ignore) {}

    // 2. data-rental-sale-price 속성
    try {
      WebElement benefitEl = driver.findElement(By.cssSelector(".benefit-info"));
      String dataPrice = benefitEl.getAttribute("data-rental-sale-price");
      if (dataPrice != null && !dataPrice.isBlank()) {
        return Integer.parseInt(dataPrice);
      }
    } catch (Exception ignore) {}

    System.out.println("구독 가격 없음");
    return -1;
  }

}
