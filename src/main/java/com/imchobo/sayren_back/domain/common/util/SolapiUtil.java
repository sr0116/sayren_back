package com.imchobo.sayren_back.domain.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class SolapiUtil {

  @Value("${solapi.api-key}")
  private String apiKey;

  @Value("${solapi.api-secret}")
  private String apiSecret;

  @Value("${solapi.base-url}")
  private String baseUrl;

  @Value("${solapi.from}")
  private String from;

  private final RedisUtil redisUtil;

  private final RestTemplate restTemplate = new RestTemplate();

  private String generateSignature(String date, String salt) throws Exception {
    String message = date + salt;
    Mac hmac = Mac.getInstance("HmacSHA256");
    hmac.init(new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256"));
    byte[] hash = hmac.doFinal(message.getBytes());
    return HexFormat.of().formatHex(hash); // JDK 17 이상
  }


  public void sendSms(String to) {
    try {
      String date = Instant.now().toString();
      String salt = UUID.randomUUID().toString().replace("-", "");
      String signature = generateSignature(date, salt);
      String phoneAuthCode = generateAuthCode();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", String.format(
        "HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
        apiKey, date, salt, signature));

      Map<String, Object> message = new HashMap<>();
      message.put("to", to);
      message.put("from", from);
      message.put("text", "세이렌 인증번호 [" + phoneAuthCode + "]를 입력하세요.");
      message.put("type", "SMS");

      Map<String, Object> body = Map.of("message", message);
      redisUtil.setPhoneAuth(phoneAuthCode, to);
      HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(
        baseUrl + "/messages/v4/send",
        request,
        String.class
      );
    } catch (Exception e) {
      throw new RuntimeException("SMS 발송 실패: " + e.getMessage(), e);
    }
  }

  private String generateAuthCode() {
    SecureRandom random = new SecureRandom();
    int number = 100000 + random.nextInt(900000); // 100000 ~ 999999
    return String.valueOf(number);
  }
}
