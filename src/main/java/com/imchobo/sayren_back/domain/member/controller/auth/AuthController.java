package com.imchobo.sayren_back.domain.member.controller.auth;


import com.imchobo.sayren_back.domain.common.service.MailService;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialLinkRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialSignupRequestDTO;
import com.imchobo.sayren_back.domain.member.service.AuthService;
import com.imchobo.sayren_back.domain.member.service.MemberService;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
  private final AuthService authService;
  private final MemberService memberService;
  private final MailService  mailService;
  private final JwtUtil jwtUtil;

  @GetMapping("me")
  public ResponseEntity<?> getUser(HttpServletRequest request) {
    return ResponseEntity.ok(authService.getUser(request));
  }

  @PostMapping("login")
  public ResponseEntity<?> login(
          @RequestBody @Valid MemberLoginRequestDTO memberLoginRequestDTO,
          HttpServletResponse response) {

    // 기존 서비스 호출 (비밀번호 검증 + 쿠키 저장 + 사용자 정보 반환)
    MemberLoginResponseDTO memberResponse = authService.login(memberLoginRequestDTO, response);

    // === 여기서 바로 토큰 생성 ===
    // memberResponse에는 id, name, roles 등 정보가 있으니, 최소한 id만으로 토큰 생성 가능
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", memberResponse.getRoles());
    claims.put("status", memberResponse.getStatus());

    String accessToken = jwtUtil.generateToken(claims, String.valueOf(memberResponse.getId()), 60 * 60); // 1시간짜리
    String refreshToken = jwtUtil.generateToken(Collections.emptyMap(), String.valueOf(memberResponse.getId()), 60 * 60 * 24 * 7); // 7일짜리

    // 임시 응답 (JSON)
    Map<String, Object> result = new HashMap<>();
    result.put("member", memberResponse);
    result.put("accessToken", accessToken);
    result.put("refreshToken", refreshToken);

    return ResponseEntity.ok(result);
  }



  @PostMapping("refresh")
  public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "SR_REFRESH", required = false) String refreshToken) {
    return ResponseEntity.ok(authService.accessToken(refreshToken));
  }

  @PostMapping("logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    authService.logout(response);
    return ResponseEntity.ok().build();
  }

  @PostMapping("social-signup")
  public ResponseEntity<?> socialSignup(@RequestBody @Valid SocialSignupRequestDTO socialSignupRequestDTO, HttpServletResponse response) {
    return ResponseEntity.ok(authService.socialSignup(socialSignupRequestDTO, response));
  }

  @PostMapping("social-link")
  public ResponseEntity<?> socialLink(@RequestBody @Valid SocialLinkRequestDTO socialLinkRequestDTO, HttpServletResponse response) {
    return ResponseEntity.ok(authService.socialLink(socialLinkRequestDTO, response));
  }

  @GetMapping("email-verify/{token}")
  public RedirectView verificationEmail(@PathVariable String token) {
    System.out.println("받은 토큰: " + token);
    String url = "http://localhost:3000/";
    if(!memberService.emailVerify(token)){
     url += "mypage";
    }

    return new RedirectView(url);
  }

  @PostMapping("email-verify")
  @PreAuthorize("!principal.emailVerified")
  public ResponseEntity<?> resendVerificationEmail() {
    mailService.emailVerification(SecurityUtil.getMemberAuthDTO().getEmail());
    return ResponseEntity.ok(Map.of("message", "success"));
  }


  @PostMapping("link/{provider}/start")
  public ResponseEntity<?> startLink(@PathVariable("provider") String provider) {
    if (!SecurityUtil.isUser()) {
      return ResponseEntity.status(401).body(Map.of("error", "UNAUTHORIZED"));
    }
    return ResponseEntity.ok(Map.of("redirectUrl", authService.socialLinkRedirectUrl(provider)));
  }
}
