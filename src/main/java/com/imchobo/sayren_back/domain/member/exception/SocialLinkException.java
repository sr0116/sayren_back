package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public class SocialLinkException extends AuthenticationException {
  @Getter
  private final SocialUser socialUser;

  public SocialLinkException(SocialUser socialUser) {
    super("LINK_REQUIRED");
    this.socialUser = socialUser;
  }

}
