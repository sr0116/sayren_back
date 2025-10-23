package com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeRoundNotFoundException extends SayrenException {
  // 없는 subscribeRoundId 조회 시
  public SubscribeRoundNotFoundException(Integer roundNo) {
    super("SUBSCRIBE_ROUND_NOT_FOUND", "구독 회차 정보를 찾을 수 없습니다. roundNo=" + roundNo);
  }

}
