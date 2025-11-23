package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

// 로그인 시, 입력한 이메일이 등록되지 않았을 때 발생
public class EmailNotFoundException extends SayrenException {
  public EmailNotFoundException() {
    super("EMAIL_NOT_FOUND", "해당 이메일로 등록된 회원이 존재하지 않습니다.");
  }
}
