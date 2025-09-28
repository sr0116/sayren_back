package com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeRoundStatusInvalidException extends SayrenException {
  //  잘못된 회차 상태 전환 시도 ( PAID/FAILED 된 회차)
  public SubscribeRoundStatusInvalidException(String status) {
    super("SUBSCRIBE_ROUND_STATUS_INVALID", "잘못된 구독 회차 상태 전환 요청입니다. status=" + status);
  }
}
