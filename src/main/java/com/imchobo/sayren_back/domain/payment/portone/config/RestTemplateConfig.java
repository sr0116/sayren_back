package com.imchobo.sayren_back.domain.payment.portone.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Log4j2
@Configuration
public class RestTemplateConfig {


  @Bean
  public RestTemplate restTemplate(ObjectMapper objectMapper, RestTemplateBuilder builder) {
    return builder
            // JSON 메시지 컨버터 우선 등록
            .additionalMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            // 로깅 인터셉터 추가
            .additionalInterceptors(loggingInterceptor())
            // Buffering 처리로 body 여러 번 읽을 수 있게
            .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
            .build();
  }

  private ClientHttpRequestInterceptor loggingInterceptor() {
    return (request, body, execution) -> {
      log.info("=== RestTemplate Request ===");
      log.info("{} {}", request.getMethod(), request.getURI());
      log.info("Headers: {}", request.getHeaders());
      log.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));

      ClientHttpResponse response = execution.execute(request, body);

      String responseBody = new BufferedReader(
              new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.joining("\n"));

      log.info("=== RestTemplate Response ===");
      log.info("Status: {} {}", response.getStatusCode(), response.getStatusText());
      log.info("Headers: {}", response.getHeaders());
      log.info("Response Body: {}", responseBody);

      return response; // Buffering 덕분에 body를 다시 읽을 수 있음
    };
  }
}