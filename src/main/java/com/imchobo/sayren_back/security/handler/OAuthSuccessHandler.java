package com.imchobo.sayren_back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.common.util.CookieUtil;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginResponseDTO;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final CookieUtil cookieUtil;
  private final JwtUtil jwtUtil;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    MemberAuthDTO memberAuthDTO = SecurityUtil.getMemberAuthDTO();

    String accessToken = jwtUtil.generateAccessToken(memberAuthDTO);
    String refreshToken = jwtUtil.generateRefreshToken(memberAuthDTO);

    cookieUtil.addRefreshTokenCookie(response, refreshToken, true);
    cookieUtil.addLoginCookie(response, true);

    MemberLoginResponseDTO loginResponseDTO = new MemberLoginResponseDTO(accessToken, "로그인 성공");

    String json = objectMapper.writeValueAsString(loginResponseDTO);

    response.setContentType("text/html;charset=UTF-8");
    response.getWriter().write(
            "<script>" +
                    "window.opener.postMessage(" + json + ", 'http://localhost:3000');" +
                    "window.close();" +
                    "</script>"
    );
  }

}
