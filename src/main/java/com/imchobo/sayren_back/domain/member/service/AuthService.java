package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  MemberLoginResponseDTO login(MemberLoginRequestDTO memberLoginRequestDTO, HttpServletResponse response);
  void logout(HttpServletResponse response);
  TokenResponseDTO accessToken(String refreshToken);
  MemberLoginResponseDTO socialSignup(SocialSignupRequestDTO socialSignupRequestDTO, HttpServletResponse response);
  MemberLoginResponseDTO socialLink(SocialLinkRequestDTO socialLinkRequestDTO, HttpServletResponse response);
}
