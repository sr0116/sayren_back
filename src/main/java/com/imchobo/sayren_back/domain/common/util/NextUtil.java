package com.imchobo.sayren_back.domain.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NextUtil {
  @Value("${next.secret}")
  private String secret;

  @Value("${next.revalidate-url}")
  private String revalidateUrl;

  private final RestTemplate restTemplate = new RestTemplate();


  private HttpHeaders headers;

  public void revalidatePaths(List<String> paths) {
    try {
      // 요청 body 구성
      Map<String, Object> body = new HashMap<>();
      body.put("secret", secret);
      body.put("paths", paths);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> response =
        restTemplate.exchange(revalidateUrl, HttpMethod.POST, requestEntity, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        System.out.println("revalidate 성공: " + response.getBody());
      } else {
        System.err.println("revalidate 실패: " + response.getStatusCode());
      }
    } catch (Exception e) {
      System.err.println("revalidate 호출 중 오류 발생: " + e.getMessage());
    }
  }
}
