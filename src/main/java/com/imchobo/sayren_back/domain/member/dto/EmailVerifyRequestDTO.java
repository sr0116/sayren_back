package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerifyRequestDTO {
  @NotBlank(message = "이메일이 필요합니다.")
  @Email
  String email;
}
