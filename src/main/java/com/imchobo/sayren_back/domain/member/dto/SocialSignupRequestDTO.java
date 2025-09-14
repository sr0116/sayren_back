package com.imchobo.sayren_back.domain.member.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialSignupRequestDTO {
  private String idToken; // 구글 token

  @AssertTrue(message = "서비스 이용약관에 동의해야 합니다")
  private boolean serviceAgree;

  @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다")
  private boolean privacyAgree;
}
