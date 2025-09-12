package com.imchobo.sayren_back.domain.common.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
  @Value("${jwt.refresh-expiration-days}")
  private long expireDays;

  public void addRefreshTokenCookie(HttpServletResponse response,
                                    String refreshToken,
                                    boolean rememberMe) {

    ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
      .from("SR_REFRESH", refreshToken) // 쿠키 이름
      .httpOnly(true)                   // JS 접근 불가
      .secure(false)                    // HTTPS 배포 시 true
      .path("/")                        // 모든 경로에서 접근 가능
      .sameSite("Lax");                // 크로스 도메인 허용

    if (rememberMe) {
      cookieBuilder.maxAge(expireDays * 24 * 60 * 60);
    } else {
      cookieBuilder.maxAge(-1); // 세션 쿠키 (브라우저 종료 시 삭제)
    }

    response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
  }
}
