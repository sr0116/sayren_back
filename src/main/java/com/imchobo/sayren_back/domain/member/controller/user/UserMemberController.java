package com.imchobo.sayren_back.domain.member.controller.user;


import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.service.MemberService;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/member")
public class UserMemberController {
  private final MemberService memberService;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  @PostMapping("register")
  public ResponseEntity<?> register(@RequestBody MemberSignupDTO memberSignupDTO) {
    memberService.register(memberSignupDTO);
    return ResponseEntity.ok(Map.of("message", "회원가입에 성공했습니다. 이메일 인증을 완료해주세요."));
  }
}
