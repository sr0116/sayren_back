package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ResetPasswordRequestDTO {
  private String token;

  @NotBlank(message = "비밀번호는 필수 입력 값입니다")
  @Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다")
  private String newPassword;
}
