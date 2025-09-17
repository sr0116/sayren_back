package com.imchobo.sayren_back.domain.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.member.dto.RedisTokenDTO;
import com.imchobo.sayren_back.domain.member.en.TokenStatus;
import com.imchobo.sayren_back.domain.member.recode.TokenMeta;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;
  private final JwtUtil jwtUtil;


  // timeUnit : 시간 단위, timeout = 시간. (ex/ 2 ,TimeUnit.SECONDS) = 2초간 유지
  public void set(String key, String value, long timeout, TimeUnit timeUnit) {
    redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
  }


  // key-value 저장 (만료시간 없음)
  public void set(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  // 값 조회
  public String get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  // 키 삭제
  public void delete(String key) {
    redisTemplate.delete(key);
  }

  // 키 확인
  public boolean hasKey(String key) {
    return redisTemplate.hasKey(key);
  }

  public void emailVerification(String token, String email) {
    set("EMAIL_VERIFY:" +  token, email, 5, TimeUnit.MINUTES);
  }

  public void saveRefreshToken(RedisTokenDTO dto) throws JsonProcessingException {

    String json = objectMapper.writeValueAsString(dto);
    TokenMeta meta = jwtUtil.getMemberIdAndTtl(dto.getToken());

    set("REFRESH_TOKEN:" + meta.memberId(), json, meta.ttlMillis(), TimeUnit.MILLISECONDS);
  }
}
