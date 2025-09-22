package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.recode.AccessToken;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  MemberLoginResponseDTO login(MemberLoginRequestDTO memberLoginRequestDTO, HttpServletResponse response);
  MemberLoginResponseDTO getUser(HttpServletRequest request);
  void logout(HttpServletResponse response, String refreshToken);
  AccessToken accessToken(HttpServletResponse response, String refreshToken);
  MemberLoginResponseDTO socialSignup(SocialSignupRequestDTO socialSignupRequestDTO, HttpServletResponse response);
  MemberLoginResponseDTO socialLink(SocialLinkRequestDTO socialLinkRequestDTO, HttpServletResponse response);
  String socialLinkRedirectUrl(String provider);
}
