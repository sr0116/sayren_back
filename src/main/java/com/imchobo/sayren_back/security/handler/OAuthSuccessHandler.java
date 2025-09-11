package com.imchobo.sayren_back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  @Value("${jwt.refresh-expiration-days}")
  private long expireDays;
  private final JwtUtil jwtUtil;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    MemberAuthDTO member = (MemberAuthDTO) authentication.getPrincipal();

    String accessToken = jwtUtil.generateAccessToken(member);
    String refreshToken = jwtUtil.generateRefreshToken(member);

    boolean rememberMe = Boolean.parseBoolean(request.getParameter("rememberMe"));

    ResponseCookie.ResponseCookieBuilder refreshCookieBuilder = ResponseCookie.from("SR_REFRESH", refreshToken)
      .httpOnly(true)
      .secure(false)
      .path("/")
      .sameSite("None");

    if (rememberMe) {
      // 장기 쿠키 (예: 7일)
      refreshCookieBuilder.maxAge(expireDays * 24 * 60 * 60);
    } else {
      // 세션 쿠키 (브라우저 닫히면 삭제)
      refreshCookieBuilder.maxAge(-1);
    }

    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookieBuilder.build().toString());

    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(
      objectMapper.writeValueAsString(Map.of(
        "SR_ACCESS", accessToken
      ))
    );
  }
}
