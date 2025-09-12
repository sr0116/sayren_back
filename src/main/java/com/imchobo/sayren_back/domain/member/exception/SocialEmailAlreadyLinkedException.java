package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

// provider의 이메일에 등록된 이메일로 회원가입시 발생
public class SocialEmailAlreadyLinkedException extends SayrenException {
  public SocialEmailAlreadyLinkedException() {
    super("SOCIAL_EMAIL_ALREADY_LINKED", "해당 이메일은 이미 계정에 연동되어있습니다.");
  }
}
