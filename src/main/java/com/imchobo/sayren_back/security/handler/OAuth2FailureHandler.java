package com.imchobo.sayren_back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.exception.SocialLinkException;
import com.imchobo.sayren_back.domain.member.exception.SocialSignupException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final ObjectMapper objectMapper;
  private final RedisUtil redisUtil;

  @Value("${app.frontend.base-url}")
  private String frontendBaseUrl;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception)
          throws IOException, ServletException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> result = new HashMap<>();

    String springState = request.getParameter("state");
    Long memberId = null;
    if (springState != null) {
      String state = redisUtil.getState(springState);
      if (state != null) {
        memberId = Long.valueOf(redisUtil.getSocialLink(state));
      }
    }

    if (exception instanceof SocialSignupException ex) {
      if (memberId != null) {
        result.put("error", "LINK_REQUIRED");
      } else {
        result.put("error", "SIGNUP_REQUIRED");
      }
      result.put("socialUser", ex.getSocialUser());
    } else if (exception instanceof SocialLinkException ex) {
      result.put("error", "LINK_REQUIRED");
      result.put("socialUser", ex.getSocialUser());
    } else {
      result.put("error", "OAUTH2_LOGIN_FAILED");
    }

    String json = objectMapper.writeValueAsString(result);

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
