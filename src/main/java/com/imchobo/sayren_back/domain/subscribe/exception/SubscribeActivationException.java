package com.imchobo.sayren_back.domain.subscribe.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeActivationException extends SayrenException {
  // 배송 완료 전 ACTIVE 전환 시도
  public SubscribeActivationException(Long subscribeId) {
    super("SUBSCRIBE_ACTIVATION_FAILED", "배송 완료 전으로 구독을 활성화할 수 없습니다. subscribeId=" + subscribeId);
  }

}
