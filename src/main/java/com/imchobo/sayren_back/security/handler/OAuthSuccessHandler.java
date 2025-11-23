package com.imchobo.sayren_back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.common.util.CookieUtil;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.service.MemberLoginHistoryService;
import com.imchobo.sayren_back.domain.member.service.MemberTokenService;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final CookieUtil cookieUtil;
  private final JwtUtil jwtUtil;
  private final ObjectMapper objectMapper;
  private final MemberMapper memberMapper;
  private final MemberTokenService memberTokenService;
  private final MemberLoginHistoryService memberLoginHistoryService;

  @Value("${app.frontend.base-url}")
  private String frontendBaseUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication)
          throws IOException, ServletException {

    MemberAuthDTO memberAuthDTO = SecurityUtil.getMemberAuthDTO();
    String json = objectMapper.writeValueAsString(
            memberTokenService.saveToken(memberAuthDTO, response, true)
    );
    memberLoginHistoryService.saveLoginHistory(memberAuthDTO.getId(), request);

    // 예: http://localhost:3000 또는 https://sayren.imchobo.com
    String frontUrl = frontendBaseUrl;

    response.setContentType("text/html;charset=UTF-8");
    response.getWriter().write(
            "<script>" +
                    "window.opener.postMessage(" + json + ", '" + frontUrl + "');" +
                    "window.close();" +
                    "</script>"
    );
  }
}
