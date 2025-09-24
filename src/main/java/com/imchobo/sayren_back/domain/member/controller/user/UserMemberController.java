package com.imchobo.sayren_back.domain.member.controller.user;


import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberTelDTO;
import com.imchobo.sayren_back.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/member")
public class UserMemberController {
  private final MemberService memberService;

  @PostMapping("register")
  public ResponseEntity<?> register(@RequestBody @Valid MemberSignupDTO memberSignupDTO) {
    memberService.register(memberSignupDTO);
    return ResponseEntity.ok(Map.of("message", "회원가입에 성공했습니다. 이메일 인증을 완료해주세요."));
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

}
