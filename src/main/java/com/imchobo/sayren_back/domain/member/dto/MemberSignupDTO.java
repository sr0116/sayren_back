package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class MemberSignupDTO {
  @NotBlank
  @Email(message="올바른 이메일 형식을 입력하세요")
  private String email;

  @Setter
  @NotBlank(message = "비밀번호는 필수 입력 값입니다")
  @Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다")
  private String password;

  @NotBlank(message = "이름은 필수 입력 값입니다")
  @Size(min = 2, message = "이름은 최소 두글자 이상이어야 합니다")
  private String name;

  @AssertTrue(message = "서비스 이용약관에 동의해야 합니다")
  private boolean serviceAgree;

  @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다")
  private boolean privacyAgree;
}