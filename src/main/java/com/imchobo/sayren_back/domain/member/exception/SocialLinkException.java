package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SocialLinkException extends SayrenException {
  public SocialLinkException() {
    super("SOCIAL_NEEDS_LINK", "기존 계정과 소셜 연동이 필요합니다.");
  }
}
