package com.imchobo.sayren_back.domain.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.common.exception.RedisParseException;
import com.imchobo.sayren_back.domain.member.dto.RedisTokenDTO;
import com.imchobo.sayren_back.domain.member.recode.LatestTerms;
import com.imchobo.sayren_back.domain.member.recode.TokenMeta;
import com.imchobo.sayren_back.domain.term.entity.Term;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

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

  // 오브젝트 저장(시간제한 x)
  public <T> void setObject (String key, T value) {
    try {
      String json = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, json);
    } catch (JsonProcessingException e) {
      throw new RedisParseException();
    }
  }

  // 오브젝트 저장(시간제한 o)
  public <T> void setObject (String key, T value, long ttl, TimeUnit ttlUnit) {
    try {
      String json = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, json, ttl, ttlUnit);
    } catch (JsonProcessingException e) {
      throw new RedisParseException();
    }
  }

  // 오브젝트 가져오기
  public <T> T getObject(String key, Class<T> clazz) {
    String json = redisTemplate.opsForValue().get(key);
    if (json == null) return null;
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new RedisParseException();
    }
  }



  public void emailVerification(String token, String email) {
    set("EMAIL_VERIFY:" +  token, email, 5, TimeUnit.MINUTES);
  }

  public String getEmailByToken(String token) {
    return get("EMAIL_VERIFY:" +  token);
  }

  public void deleteEmailToken(String token) {
    delete("EMAIL_VERIFY:" +  token);
  }

  public void setState(String springState, String myState){
    set("STATE:" +  springState, myState, 5, TimeUnit.MINUTES);
  }

  public String getState(String springState){
    String state = get("STATE:" +  springState);
    delete("STATE:" +  springState);
    return state;
  }

  public void setSocialLink(String state, Long memberId) {
    set("SOCIAL_LINK:" + state, memberId.toString(), 5, TimeUnit.MINUTES);
  }

  public String getSocialLink(String state) {
    String socialLink = get("SOCIAL_LINK:" + state);
    delete("SOCIAL_LINK:" + state);
    return socialLink;
  }



  public void setPhoneAuth(String phoneAuthCode, String tel) {
    set("PHONE_AUTH:" +  phoneAuthCode, tel, 5, TimeUnit.MINUTES);
  }

  public String getPhoneAuth(String phoneAuthCode) {
    String tel = get("PHONE_AUTH:" + phoneAuthCode);
    delete("PHONE_AUTH:" + phoneAuthCode);
    return tel;
  }


  @SneakyThrows
  public void setRefreshToken(RedisTokenDTO dto) {
    TokenMeta meta = jwtUtil.getMemberIdAndTtl(dto.getToken());
    setObject("REFRESH_TOKEN:" + meta.memberId(), dto, meta.ttlMillis(), TimeUnit.MILLISECONDS);
  }

  public RedisTokenDTO getRefreshToken(Long memberId) {
    return getObject("REFRESH_TOKEN:" + memberId, RedisTokenDTO.class);
  }

  public void deleteRefreshToken(Long memberId) {
    delete("REFRESH_TOKEN:" + memberId);
  }

  public void setTermLatest(LatestTerms latestTerms) {
    setObject("SERVICE_TERM", latestTerms.service());
    setObject("PRIVACY_TERM", latestTerms.privacy());
  }

  public LatestTerms getLatestTerms() {
    Term service = getObject("SERVICE_TERM", Term.class);
    Term privacy = getObject("PRIVACY_TERM", Term.class);

    if (service == null || privacy == null) {
      throw new IllegalStateException("Redis에 최신 약관이 존재하지 않습니다.");
    }

    return new LatestTerms(service, privacy);
  }


}
