package com.imchobo.sayren_back.domain.member.dto;

import com.imchobo.sayren_back.domain.member.en.Provider;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
public class SocialSignupRequestDTO {
  @NotEmpty(message = "소셜 프로필 정보가 필요합니다")
  private Map<String, Object> attributes;

  @NotNull(message = "소셜 로그인 제공자 정보가 필요합니다")
  private Provider provider;

  @AssertTrue(message = "서비스 이용약관에 동의해야 합니다")
  private boolean serviceAgree;

  @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다")
  private boolean privacyAgree;
}
