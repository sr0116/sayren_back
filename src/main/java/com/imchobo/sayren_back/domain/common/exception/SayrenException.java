package com.imchobo.sayren_back.domain.common.exception;

import lombok.Getter;

@Getter
public class SayrenException extends RuntimeException {
  private final String errorCode;

  public SayrenException(String errorCode, String message)
  {
    super(message);
    this.errorCode = errorCode;
  }
}
