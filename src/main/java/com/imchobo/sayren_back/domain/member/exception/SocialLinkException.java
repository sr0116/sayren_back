package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.member.en.Provider;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public class SocialLinkException extends AuthenticationException {
  @Getter
  private final Map<String, Object> attributes;
  @Getter
  private final Provider provider;

  public SocialLinkException(Map<String, Object> attributes, Provider provider) {
    super("LINK_REQUIRED");
    this.attributes = attributes;
    this.provider = provider;
  }

}
