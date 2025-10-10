package com.imchobo.sayren_back.domain.crawling.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.product.entity.ProductTag;
import com.imchobo.sayren_back.domain.product.repository.ProductRepository;
import com.imchobo.sayren_back.domain.product.repository.ProductTagRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    WebElement placeholder = driver.findElement(By.className("iw_placeholder"));
    String html = placeholder.getDomProperty("outerHTML");
    Document doc = Jsoup.parse(html);

    StringBuilder result = new StringBuilder();

    int textCount = 0;
    int imgCount = 0;

    for (Element comp : doc.select(".iw_component")) {

      // 텍스트 추출
      for (Element t : comp.select(".txt, p, span, strong")) {
        if (textCount >= 10) break;
        String text = t.text().trim();
        if (!text.isEmpty()) {
          result.append("<p>").append(text).append("</p>\n");
          textCount++;
        }
      }

      // 이미지 추출
      for (Element img : comp.select("img")) {
        if (imgCount >= 5) break;

        String src = img.hasAttr("data-pc-src") ? img.attr("data-pc-src") : img.attr("src");
        if (src == null || src.isBlank()) continue;
        if (!src.startsWith("http")) src = "https://www.lge.co.kr" + src;

        // 추가: URL 인코딩(공백, 괄호, + 기호 등 처리)
        src = src.replace(" ", "%20");
        src = src.replaceAll("\\(", "%28").replaceAll("\\)", "%29");
        src = src.replaceAll("\\+", "%2B");


        String alt = img.attr("alt").toLowerCase();
        if (alt.contains("손가락") || alt.contains("배경") || alt.contains("아이콘")
                || alt.contains("카드") || alt.contains("배너") || alt.contains("로고"))
          continue;

        try {
          String s3Url = s3UploadUtil.getFullUrl(s3UploadUtil.upload(src));
          result.append("<img src=\"").append(s3Url)
                  .append("\" style=\"width:100%;height:auto;display:block;\"/>\n");
          imgCount++;
        } catch (Exception e) {
          System.err.println("이미지 업로드 실패: " + e.getMessage());
        }
      }

      if (textCount >= 12 && imgCount >= 10) break;
    }

    return replaceBrandName(result.toString());
  }

  public List<String> getDescriptionImageUrls(WebDriver driver) throws Exception {
        WebElement firstBlock = driver.findElement(By.className("iw_placeholder"));
        String html = firstBlock.getDomProperty("outerHTML");
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);

        List<String> imageUrls = new ArrayList<>();

        for (org.jsoup.nodes.Element img : doc.select("img")) {
            String src = img.attr("src");

            if (src == null || src.isBlank()) continue;

            if (!src.startsWith("http")) {
                src = "https://www.lge.co.kr" + src;
            }

            // URL 안전하게 인코딩
            String newSrc = src.replace(" ", "%20")
                    .replaceAll("\\(", "%28")
                    .replaceAll("\\)", "%29")
                    .replaceAll("\\+", "%2B");

          try {
            // S3 업로드 후 전체 URL 반환
            String fullUrl = s3UploadUtil.getFullUrl(s3UploadUtil.upload(newSrc));

            // 이미 완성된 전체 S3 URL을 바로 사용 (split 불필요)
            imageUrls.add(fullUrl);
            System.out.println("S3 업로드 결과 fullUrl = " + fullUrl);

          } catch (Exception e) {
            System.err.println("이미지 업로드 실패: " + e.getMessage());
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
