package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Member2FARequestDTO {
  @Pattern(regexp = "^[0-9]{6}$", message = "OTP는 6자리 숫자여야 합니다.")
  String otp;
}
