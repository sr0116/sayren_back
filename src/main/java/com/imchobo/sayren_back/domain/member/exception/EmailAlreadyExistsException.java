package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

// 회원가입시 이미 가입된 이메일 에러
public class EmailAlreadyExistsException extends SayrenException {
  public EmailAlreadyExistsException(){
    super("EMAIL_ALREADY_EXISTS", "이미 가입된 이메일입니다.");
  }
}
