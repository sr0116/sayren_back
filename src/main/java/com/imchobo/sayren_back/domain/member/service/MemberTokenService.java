package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.MemberLoginResponseDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberToken;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberTokenService {
  MemberLoginResponseDTO saveToken(Member member, HttpServletResponse response, boolean rememberMe);
  Long validateAndGetMemberId(String refreshToken);
  MemberToken getMemberToken(Long memberId);
  void deleteMemberToken(Long memberId);
  void deleteMemberToken(String refreshToken);
}
