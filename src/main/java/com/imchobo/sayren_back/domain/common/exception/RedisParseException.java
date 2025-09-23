package com.imchobo.sayren_back.domain.common.exception;

public class RedisParseException extends RedisException {
  public RedisParseException() {
    super("REDIS_PARSE_ERROR", "Redis 데이터 파싱에 실패했습니다.");
  }
}
