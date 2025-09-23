package com.imchobo.sayren_back.domain.common.exception;

public class RedisKeyNotFoundException extends RedisException {
  public RedisKeyNotFoundException() {
    super("REDIS_KEY_NOT_FOUND", "Redis에서 요청한 키를 찾을 수 없습니다.");
  }
}
