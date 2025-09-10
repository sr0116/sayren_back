package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;

public interface MemberService {
  void register(MemberSignupDTO memberSignupDTO);

}
