package com.imchobo.sayren_back.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imchobo.sayren_back.domain.member.exception.SocialLinkException;
import com.imchobo.sayren_back.domain.member.exception.SocialSignupException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> result = new HashMap<>();

    if (exception instanceof SocialSignupException ex) {
      result.put("error", "SIGNUP_REQUIRED");
      result.put("attributes", ex.getAttributes());
      result.put("provider", ex.getProvider());
    } else if (exception instanceof SocialLinkException ex) {
      result.put("error", "LINK_REQUIRED");
      result.put("attributes", ex.getAttributes());
      result.put("provider", ex.getProvider());
    } else {
      result.put("error", "OAUTH2_LOGIN_FAILED");
    }

    String json = objectMapper.writeValueAsString(result);

    response.setContentType("text/html;charset=UTF-8");
    response.getWriter().write(
            "<script>" +
                    "window.opener.postMessage(" + json + ", 'http://localhost:3000');" +
                    "window.close();" +
                    "</script>"
    );
  }
}
