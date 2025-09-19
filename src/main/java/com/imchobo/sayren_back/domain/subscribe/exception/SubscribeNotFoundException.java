package com.imchobo.sayren_back.domain.subscribe.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeNotFoundException extends SayrenException {
  // 구독 아이디 조회 실패
  public SubscribeNotFoundException(Long subscribeId) {
    super("SUBSCRIBE_NOT_FOUND", "구독 정보를 찾을 수 없습니다. subscribeId = " + subscribeId);
  }

  // 구독 아이디 조회 실패 (아이디 없이 단순 메시지)
  public SubscribeNotFoundException() {
    super("SUBSCRIBE_NOT_FOUND", "구독 정보를 찾을 수 없습니다.");
  }

}
