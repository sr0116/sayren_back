package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  MemberLoginResponseDTO login(MemberLoginRequestDTO memberLoginRequestDTO, HttpServletResponse response);
  void logout(HttpServletResponse response);
  TokenResponseDTO accessToken(String refreshToken);
  MemberLoginResponseDTO socialSignup(SocialSignupRequestDTO socialSignupRequestDTO, HttpServletResponse response);
  MemberLoginResponseDTO socialLink(SocialLinkRequestDTO socialLinkRequestDTO, HttpServletResponse response);
  String socialLinkRedirectUrl(String provider);
}
