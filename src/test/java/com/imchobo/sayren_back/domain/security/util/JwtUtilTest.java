package com.imchobo.sayren_back.domain.security.util;

import com.imchobo.sayren_back.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class JwtUtilTest {
  @Autowired
  private JwtUtil jwtUtil;

  @Test
  @DisplayName("Access Token 생성 및 검증")
  void accessToken() {
    // given
    String subject = "testUser";

    // when
    String token = jwtUtil.generateAccessToken(subject);
    Claims claims = jwtUtil.validateToken(token);

    log.info(token);
    log.info(claims);
  }

  @Test
  @DisplayName("Refresh Token 생성 및 검증")
  void refreshToken() {
    // given
    String subject = "testUser";

    // when
    String token = jwtUtil.generateRefreshToken(subject);
    Claims claims = jwtUtil.validateToken(token);

    log.info(token);
    log.info(claims);
  }
}
