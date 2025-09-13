package com.imchobo.sayren_back.domain.member.controller.auth;

import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.TokenResponseDTO;
import com.imchobo.sayren_back.domain.member.service.AuthService;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("login")
  public ResponseEntity<?> login(@RequestBody @Valid MemberLoginRequestDTO memberLoginRequestDTO, HttpServletResponse response) {
    return ResponseEntity.ok(authService.login(memberLoginRequestDTO, response));
  }


  @PostMapping("refresh")
  public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "SR_REFRESH", required = false) String refreshToken) {
    TokenResponseDTO accessToken = authService.accessToken(refreshToken);
    if(accessToken == null) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(accessToken);
  }
}
