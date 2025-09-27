package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

// 비밀번호 변경시 사용중인비밀번호
public class PasswordAlreadyUseException extends SayrenException {
  public PasswordAlreadyUseException(){
    super("PASSWORD_ALREADY_USED", "변경할 비밀번호가 현재 비밀번호와 동일합니다.");
  }
}
