package com.imchobo.sayren_back.domain.member.controller.user;


import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.service.MemberProviderService;
import com.imchobo.sayren_back.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/member")
@Log4j2
public class UserMemberController {
  private final MemberService memberService;
  private final MemberProviderService memberProviderService;

  @PostMapping("register")
  public ResponseEntity<?> register(@RequestBody @Valid MemberSignupDTO memberSignupDTO) {
    memberService.register(memberSignupDTO);
    return ResponseEntity.ok(Map.of("message", "회원가입에 성공했습니다. 이메일 인증을 완료해주세요."));
  }

  @PostMapping("email-verify")
  public ResponseEntity<?> emailSend(@RequestBody @Valid EmailVerifyRequestDTO emailVerifyRequestDTO) {
    memberService.checkMail(emailVerifyRequestDTO);
    return ResponseEntity.ok(Map.of("message", "인증 메일을 발송했습니다."));
  }

  @PostMapping("modify-tel")
  public ResponseEntity<?> modifyTel(@RequestBody @Valid MemberTelDTO memberTelDTO) {
    memberService.modifyTel(memberTelDTO);
    return ResponseEntity.ok(Map.of("message", "휴대전화번호 수정 완료."));
  }

  @PostMapping("send-tel")
  public ResponseEntity<?> sendTel(@RequestParam String tel){
    memberService.sendTel(tel);
    return ResponseEntity.ok(Map.of("message", "인증번호 전송 완료."));
  }

  @PostMapping("find-email")
  public ResponseEntity<?> findEmail(@RequestBody @Valid MemberTelDTO memberTelDTO){
    return ResponseEntity.ok(memberService.findEmail(memberTelDTO));
  }

  @GetMapping("get-tel")
  public ResponseEntity<?> getTel(){
    return ResponseEntity.ok(memberService.getTel());
  }


  @PostMapping("find-pw")
  public ResponseEntity<?> findPassword(@RequestBody @Valid FindPasswordRequestDTO findPasswordRequestDTO){
    memberService.findPassword(findPasswordRequestDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @GetMapping("reset-pw/{token}")
  public RedirectView resetPassword(@PathVariable String token) {
    String url = "http://localhost:3000/member/reset-pw/" + token;
    return new RedirectView(url);
  }

  @PatchMapping("reset-pw")
  public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO) {
    log.info(resetPasswordRequestDTO);
    memberService.changePassword(resetPasswordRequestDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @GetMapping("social-list")
  public ResponseEntity<?> getSocialList(){
    return ResponseEntity.ok(memberProviderService.getMemberSocialList());
  }

  @PostMapping("social-disconnect")
  public ResponseEntity<?> socialDisconnect(@RequestBody @Valid SocialDisconnectDTO socialDisconnectDTO) {
    memberProviderService.disconnect(socialDisconnectDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @GetMapping("signup-next")
  public ResponseEntity<?> signupNext(@RequestParam String token) {
    return ResponseEntity.ok(Map.of("email", memberService.signupNext(token)));
  }

  @PatchMapping("change-name")
  public ResponseEntity<?> changeName(@RequestBody @Valid ChangeNameDTO changeNameDTO) {
    return ResponseEntity.ok(memberService.changeName(changeNameDTO));
  }


  @PostMapping("check-pw")
  public ResponseEntity<?> checkPassword(@RequestBody @Valid PasswordCheckDTO passwordCheckDTO) {
    log.info(passwordCheckDTO);
    memberService.passwordCheck(passwordCheckDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

}
