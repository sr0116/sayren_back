package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.MemberLoginRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.TokenResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  MemberLoginResponseDTO login(MemberLoginRequestDTO memberLoginRequestDTO, HttpServletResponse response);
  void logout(HttpServletResponse response);
  TokenResponseDTO accessToken(String refreshToken);
}
