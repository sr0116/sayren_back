package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.Member2FARegisterDTO;
import com.imchobo.sayren_back.domain.member.recode.QrCode;

public interface Member2FAService {
  QrCode getQrCode();
  void register(Member2FARegisterDTO member2FARegisterDTO);
  boolean verify(String secret, String otp);
  void delete(Long memberId);
}
