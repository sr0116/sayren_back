package com.imchobo.sayren_back.domain.member.controller.auth;


import com.imchobo.sayren_back.domain.common.service.MailService;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialLinkRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialSignupRequestDTO;
import com.imchobo.sayren_back.domain.member.service.AuthService;
import com.imchobo.sayren_back.domain.member.service.MemberService;
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

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
  private final AuthService authService;
  private final MemberService memberService;
  private final MailService  mailService;

  @GetMapping("me")
  public ResponseEntity<?> getUser(HttpServletRequest request) {
    return ResponseEntity.ok(authService.getUser(request));
  }

  @PostMapping("login")
  public ResponseEntity<?> login(@RequestBody @Valid MemberLoginRequestDTO memberLoginRequestDTO, HttpServletResponse response, HttpServletRequest request) {
    return ResponseEntity.ok(authService.login(memberLoginRequestDTO, response, request));
  }


  @PostMapping("refresh")
  public ResponseEntity<?> refreshAccessToken(HttpServletResponse response, @CookieValue(name = "SR_REFRESH", required = false) String refreshToken) {
    return ResponseEntity.ok(authService.accessToken(response, refreshToken));
  }

  @PostMapping("logout")
  public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue(name = "SR_REFRESH", required = false) String refreshToken) {
    authService.logout(response, refreshToken);
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
    log.info("받은 토큰: {}", token);
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
