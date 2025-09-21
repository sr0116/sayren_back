package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberTelModifyDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;

public interface MemberService {
  void register(MemberSignupDTO memberSignupDTO);
  Member findByEmail(String email);
  Member findById(Long id);
  boolean emailVerify(String token);
  void modifyTel(MemberTelModifyDTO  memberTelModifyDTO);
  void sendTel(String newTel);
}
