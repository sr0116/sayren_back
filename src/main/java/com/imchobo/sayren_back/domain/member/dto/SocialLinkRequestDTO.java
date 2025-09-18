package com.imchobo.sayren_back.domain.member.dto;

import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
public class SocialLinkRequestDTO {
  @NotNull(message = "소셜 프로필 정보가 필요합니다")
  private SocialUser socialUser;

  @Setter
  @NotBlank(message = "비밀번호는 필수 입력 값입니다")
  @Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다")
  private String password;
}
