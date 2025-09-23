package com.imchobo.sayren_back.domain.common.exception;

public class RedisConnectionException extends RedisException {
  public RedisConnectionException() {
    super("REDIS_CONNECTION_ERROR", "Redis 서버 연결에 실패했습니다.");
  }
}
