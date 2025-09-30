package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SocialDisconnectException extends SayrenException {
  public SocialDisconnectException() {
    super("SOCIAL_DISCONNECT_FAIL", "비밀번호가 없는 마지막 소셜계정은 해제가 불가능합니다.");
  }
}
