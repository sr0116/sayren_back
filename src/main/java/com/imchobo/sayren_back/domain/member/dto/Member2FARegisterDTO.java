package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Member2FARegisterDTO {
  @Pattern(regexp = "^[0-9]{6}$", message = "OTP는 6자리 숫자여야 합니다.")
  String otp;
}
