package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;
import com.imchobo.sayren_back.domain.member.en.Provider;

public class SocialSignupException extends SayrenException {

  public SocialSignupException() {
    super("SOCIAL_NEEDS_SIGNUP", "소셜 계정 신규 가입이 필요합니다.");
  }
}
