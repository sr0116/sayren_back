package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class MemberTelModifyDTO {
  @NotBlank
  @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 숫자 6자리여야 합니다.")
  String phoneAuthCode;
  @NotBlank
  String newTel;
}
