package com.imchobo.sayren_back.domain.common.util;

import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
  private final SecretKey key;

  public JwtUtil(@Value("${jwt.secret}") String secret) {
    key = Keys.hmacShaKeyFor(secret.getBytes());
  }
  @Value("${jwt.expiration-minutes}")
  private long expireMinutes;

  @Value("${jwt.refresh-expiration-days}")
  private long expireDays;

  public String generateToken(Map<String, Object> claims, String subject, long expireSeconds) {
    Instant now = Instant.now();

    return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expireSeconds)))
            .signWith(key)
            .compact();
  }

  public String generateAccessToken(MemberAuthDTO member) { // 짧은 시간
    long expireSeconds = expireMinutes * 60;

    Map<String, Object> claims = new HashMap<>();
    claims.put("name", member.getRealName());
    claims.put("status", member.getStatus());
    claims.put("roles", member.getRoles());
    claims.put("emailVerified", member.getEmailVerified());

    return generateToken(claims, member.getEmail(), expireSeconds);
  }

  // RefreshToken은 멤버 pk 가지고 있음.
  public String generateRefreshToken(MemberAuthDTO member) { // 긴 시간
    long expireSeconds = expireDays * 24 * 60 * 60;
    return generateToken(Collections.emptyMap(), String.valueOf(member.getId()), expireSeconds);
  }


  public Claims validateToken(String token) {
    return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public String resolveToken(HttpServletRequest request) { // 토큰 파싱
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
