package com.imchobo.sayren_back.domain.subscribe.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeStatusInvalidException extends SayrenException {
  //  잘못된 상태 전환 시도 (CANCELED 같은 상태 변경)
  public SubscribeStatusInvalidException(String status) {
    super("SUBSCRIBE_STATUS_INVALID", "잘못된 구독 상태 전환 요청입니다. status=" + status);
  }

}
