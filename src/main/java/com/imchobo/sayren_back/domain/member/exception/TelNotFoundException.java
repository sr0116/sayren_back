package com.imchobo.sayren_back.domain.member.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

// 로그인 시, 입력한 전화번호가 등록되지 않았을 때 발생
public class TelNotFoundException extends SayrenException {
  public TelNotFoundException() {
    super("TEL_NOT_FOUND", "해당 전화번호로 등록된 회원이 존재하지 않습니다.");
  }
}
