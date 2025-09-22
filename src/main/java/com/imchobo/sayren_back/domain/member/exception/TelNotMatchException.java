package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

// 로그인 시, 입력한 전화번호가 등록되지 않았을 때 발생
public class TelNotMatchException extends SayrenException {
  public TelNotMatchException() {
    super("TEL_NOT_MATCH", "휴대폰번호가 일치하지 않습니다.");
  }
}
