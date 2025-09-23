package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public class SocialSignupException extends AuthenticationException {
  @Getter
  private final SocialUser socialUser;


  public SocialSignupException(SocialUser socialUser) {
    super("SIGNUP_REQUIRED");
    this.socialUser = socialUser;
  }
}