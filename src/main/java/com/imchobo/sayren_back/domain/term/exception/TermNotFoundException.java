package com.imchobo.sayren_back.domain.term.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class TermNotFoundException extends SayrenException {
  public TermNotFoundException() {
    super("TERM_NOT_FOUND", "활성화된 TERM이 없습니다.");
  }
}
