package com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeRoundCreationException extends SayrenException {
  //  첫 회차 생성 실패 -> due_date 누락, DB 오류 상황
  public SubscribeRoundCreationException(String message) {
    super("SUBSCRIBE_ROUND_CREATION_FAILED", "구독 회차 생성 중 오류가 발생했습니다. " + message);
  }
}
