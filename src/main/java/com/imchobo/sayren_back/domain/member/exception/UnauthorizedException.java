package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class UnauthorizedException extends SayrenException {
  public UnauthorizedException(String message) {
    super("UNAUTHORIZED", message);
  }
}