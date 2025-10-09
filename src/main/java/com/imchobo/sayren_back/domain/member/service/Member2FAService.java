package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.Member2FARequestDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.AdminSelectMemberIdDTO;
import com.imchobo.sayren_back.domain.member.recode.QrCode;

public interface Member2FAService {
  QrCode getQrCode();
  void register(Member2FARequestDTO member2FARequestDTO);
  boolean verify(String secret, String otp);
  void delete(AdminSelectMemberIdDTO adminSelectMemberIdDTO);
  void delete(Long memberId);
  void delete();
  void checkOtp(Member2FARequestDTO member2FARequestDTO);
  void read();
}
