package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class OTPNotMatchException extends SayrenException {
  public OTPNotMatchException() {
    super("OTP_NOT_MATCH", "OTP가 맞지 않습니다.");
  }
}
