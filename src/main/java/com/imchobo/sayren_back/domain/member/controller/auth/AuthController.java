package com.imchobo.sayren_back.domain.member.controller.auth;


import com.imchobo.sayren_back.domain.common.util.MailUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialLinkRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialSignupRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.TokenResponseDTO;
import com.imchobo.sayren_back.domain.member.service.AuthService;
import com.imchobo.sayren_back.domain.member.service.MemberService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
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
  private final MailUtil mailUtil;


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
    mailUtil.emailVerification(SecurityUtil.getMemberAuthDTO().getEmail());
    return ResponseEntity.ok(Map.of("message", "success"));
  }
}
