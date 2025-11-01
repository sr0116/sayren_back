package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

// 로그인 시, 비밀번호가 틀렸을 때 발생
public class InvalidPasswordException extends SayrenException {
  public InvalidPasswordException() {
    super("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.");
  }
}
