package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.FindEmailResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberTelDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;

import java.util.Map;

public interface MemberService {
  void register(MemberSignupDTO memberSignupDTO);
  Member findByEmail(String email);
  Member findById(Long id);
  boolean emailVerify(String token);
  void modifyTel(MemberTelDTO memberTelDTO);
  void sendTel(String newTel);
  FindEmailResponseDTO findEmail(MemberTelDTO memberTelDTO);
  Member telVerify(MemberTelDTO memberTelDTO);
  Map<?, ?> getTel();
}
