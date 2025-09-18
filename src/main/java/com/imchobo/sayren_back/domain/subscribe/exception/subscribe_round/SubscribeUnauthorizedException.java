package com.imchobo.sayren_back.domain.subscribe.exception.subscribe_round;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class SubscribeUnauthorizedException extends SayrenException {
  //  구독 회원 != SecurityContext의 로그인 회원
  public SubscribeUnauthorizedException(Long memberId) {
    super("SUBSCRIBE_UNAUTHORIZED", "구독회차 접근 권한이 없습니다. memberId=" + memberId);
  }
}
