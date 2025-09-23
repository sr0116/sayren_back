package com.imchobo.sayren_back.domain.subscribe.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeCreationException extends SayrenException {
  // /prepare 단계에서 구독 생성 실패
  public SubscribeCreationException(String message) {
    super("SUBSCRIBE_CREATION_FAILED", "구독 생성 중 오류가 발생했습니다. " + message);
  }

}
