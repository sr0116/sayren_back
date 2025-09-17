package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;

public interface MemberService {
  void register(MemberSignupDTO memberSignupDTO);
  Member findByEmail(String email);
  boolean emailVerify(String token);
}
