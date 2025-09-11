package com.imchobo.sayren_back.domain.payment.portone.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.payment.portone.config.PortOneClientConfig;
import com.imchobo.sayren_back.domain.payment.portone.dto.TokenRequest;
import com.imchobo.sayren_back.domain.payment.portone.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortOneTokenClient {

  private final PortOneClientConfig config;   // PortOne API Key, Secret
  private final RestTemplate restTemplate;    // RestTemplateConfig에서 주입

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * PortOne 액세스 토큰 발급
   */
  public String getAccessToken() {
    String url = "https://api.iamport.kr/users/getToken";

    // 요청 DTO
    TokenRequest body = new TokenRequest(config.getApiKey(), config.getApiSecret());

    // 헤더
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<TokenRequest> entity = new HttpEntity<>(body, headers);

    log.info("보내는 JSON = {}", toJson(body));

    // API 호출
    ResponseEntity<TokenResponse> res = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            TokenResponse.class
    );

    log.info("PortOne 토큰 응답 객체 = {}", toJson(res.getBody()));

    // --- 응답 검증 ---
    if (res.getBody() == null) {
      throw new RuntimeException("PortOne 토큰 응답 Body가 null: " + res);
    }

    if (res.getBody().getCode() != null && res.getBody().getCode() != 0) {
      throw new RuntimeException("PortOne 토큰 발급 실패: " + res.getBody().getMessage());
    }

    if (res.getBody().getResponse() == null || res.getBody().getResponse().getAccessToken() == null) {
      throw new RuntimeException("PortOne 토큰 응답에 access_token 없음: " + toJson(res.getBody()));
    }

    return res.getBody().getResponse().getAccessToken();
  }

  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      return String.valueOf(obj);
    }
  }
}
