package com.imchobo.sayren_back.domain.member.controller.auth;

import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.service.AuthService;
import com.imchobo.sayren_back.domain.member.service.Member2FAService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

  private final AuthService authService;
  private final Member2FAService member2faService;

  @Value("${app.frontend.base-url}")
  private String frontendBaseUrl;

  @GetMapping("me")
  @Operation(
          summary = "회원 정보 가져오기",
          description = "엑세스 토큰정보를 확인한 후 스프링 컨텍스트에서 회원 정보를 불러옵니다."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "회원 불러오기 성공",
                  content = @Content(schema = @Schema(implementation = MemberLoginResponseDTO.class))),
          @ApiResponse(responseCode = "400", description = "엑세스 토큰 만료 or 없음")
  })
  public ResponseEntity<?> getUser() {
    return ResponseEntity.ok(authService.getUser());
  }

  @PostMapping("login")
  public ResponseEntity<?> login(@RequestBody @Valid MemberLoginRequestDTO memberLoginRequestDTO,
                                 HttpServletResponse response,
                                 HttpServletRequest request) {
    return ResponseEntity.ok(authService.login(memberLoginRequestDTO, response, request));
  }

  @PostMapping("refresh")
  public ResponseEntity<?> refreshAccessToken(HttpServletResponse response,
                                              @CookieValue(name = "SR_REFRESH", required = false) String refreshToken) {
    return ResponseEntity.ok(authService.accessToken(response, refreshToken));
  }

  @PostMapping("logout")
  public ResponseEntity<?> logout(HttpServletResponse response,
                                  @CookieValue(name = "SR_REFRESH", required = false) String refreshToken) {
    authService.logout(response, refreshToken);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @PostMapping("social-signup")
  public ResponseEntity<?> socialSignup(@RequestBody @Valid SocialSignupRequestDTO socialSignupRequestDTO,
                                        HttpServletResponse response,
                                        HttpServletRequest request) {
    return ResponseEntity.ok(authService.socialSignup(socialSignupRequestDTO, response, request));
  }

  @PostMapping("social-link")
  public ResponseEntity<?> socialLink(@RequestBody @Valid SocialLinkRequestDTO socialLinkRequestDTO,
                                      HttpServletResponse response,
                                      HttpServletRequest request) {
    return ResponseEntity.ok(authService.socialLink(socialLinkRequestDTO, response, request));
  }

  @GetMapping("email-verify/{token}")
  public RedirectView verificationEmail(@PathVariable String token) {
    // 예: http://localhost:3000/member/signup/{token}
    //   또는 https://sayren.imchobo.com/member/signup/{token}
    String url = frontendBaseUrl + "/member/signup/" + token;
    return new RedirectView(url);
  }

  @PostMapping("link/{provider}/start")
  public ResponseEntity<?> startLink(@PathVariable("provider") String provider) {
    if (!SecurityUtil.isUser()) {
      return ResponseEntity.status(401).body(Map.of("error", "UNAUTHORIZED"));
    }
    return ResponseEntity.ok(Map.of("redirectUrl", authService.socialLinkRedirectUrl(provider)));
  }

  @GetMapping("reset-pw/validate")
  public ResponseEntity<?> resetPassword(@RequestParam String token) {
    authService.hasResetPasswordKey(token);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  // qr코드 가져오기(2차인증 등록용)
  @GetMapping("2fa-qr")
  public ResponseEntity<?> get2faQR() {
    return ResponseEntity.ok(member2faService.getQrCode());
  }

  // 2차인증 등록
  @PostMapping("create-2fa")
  public ResponseEntity<?> create2fa(@RequestBody @Valid Member2FARequestDTO member2FARequestDTO) {
    log.info(member2FARequestDTO);
    member2faService.register(member2FARequestDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  // 2차인증 검증
  @PostMapping("check-2fa")
  public ResponseEntity<?> check2fa(@RequestBody @Valid Member2FARequestDTO member2FARequestDTO) {
    log.info(member2FARequestDTO);
    member2faService.checkOtp(member2FARequestDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @GetMapping("read-2fa")
  public ResponseEntity<?> read2fa() {
    member2faService.read();
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @DeleteMapping("delete-2fa")
  public ResponseEntity<?> delete2fa() {
    member2faService.delete();
    return ResponseEntity.ok(Map.of("message", "success"));
  }
}
