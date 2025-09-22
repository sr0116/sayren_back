package com.imchobo.sayren_back.domain.common.exception;

public class RedisException extends SayrenException {
  public RedisException(String errorCode, String message) {
    super(errorCode, message);
  }
}
