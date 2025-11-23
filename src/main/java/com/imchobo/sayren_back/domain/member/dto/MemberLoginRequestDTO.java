package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginRequestDTO {
  @NotBlank(message = "이메일 또는 휴대폰 번호를 입력하세요")
  private String username; // 이메일 or 휴대폰 번호

  @NotBlank(message = "비밀번호는 필수 입력 값입니다")
  @Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다")
  private String password;

  private boolean rememberMe = false;
}
