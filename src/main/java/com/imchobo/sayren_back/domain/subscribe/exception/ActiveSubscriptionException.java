package com.imchobo.sayren_back.domain.subscribe.exception;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;

public class ActiveSubscriptionException extends SayrenException {
  // 관리자용 (특정 memberId로 검증할 때)
  public ActiveSubscriptionException(Long memberId) {
    super(
            "ACTIVE_SUBSCRIPTION_EXISTS",
            "해당 회원은 현재 구독 중이거나 결제 대기 중입니다. memberId=" + memberId
    );
  }

  // 사용자용 (SecurityContext 기반 currentMember)
  public ActiveSubscriptionException() {
    super(
            "ACTIVE_SUBSCRIPTION_EXISTS",
            "현재 사용자는 구독 중 상태이므로 탈퇴 또는 신규 구독이 불가합니다."
    );
  }
}
