package com.imchobo.sayren_back.domain.payment.portone.client;

import com.imchobo.sayren_back.domain.payment.portone.dto.TokenRequest;
import com.imchobo.sayren_back.domain.payment.portone.config.PortOneClientConfig;
import com.imchobo.sayren_back.domain.payment.portone.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortOneTokenClient {

  private final PortOneClientConfig config;
  private final RestTemplate restTemplate = new RestTemplate();


  /// (임시) 액세스 토큰 발급
  public String getAccessToken() {
    String url = "https://api.iamport.kr/users/getToken";

    // DTO로 body 생성
    TokenRequest body = new TokenRequest(config.getApiKey(), config.getApiSecret());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<TokenRequest> entity = new HttpEntity<>(body, headers);
    ResponseEntity<TokenResponse> res =
            restTemplate.postForEntity(url, entity, TokenResponse.class);

    log.info("PortOne 토큰 요청 DTO: {}", body);

    if (res.getBody() == null || res.getBody().getResponse() == null) {
      throw new RuntimeException("PortOne 토큰 발급 실패: " + res);
    }
    return res.getBody().getResponse().getAccessToken();
  }


}
