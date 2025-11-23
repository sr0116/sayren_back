package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class Not2FAUserException extends SayrenException {
  public Not2FAUserException() {
    super("NOT_2FA_USER", "2차인증 유저가 아닙니다.");
  }
}
