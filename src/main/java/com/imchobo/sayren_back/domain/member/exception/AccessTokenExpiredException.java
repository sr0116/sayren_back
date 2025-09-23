package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class AccessTokenExpiredException extends SayrenException {

  public AccessTokenExpiredException() {
    super("TOKEN_EXPIRED", "Access Token이 만료되었습니다.");
  }
}
