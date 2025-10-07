package com.imchobo.sayren_back.domain.payment.refund.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefundApprovedEvent {
  private final Long orderItemId;
  private final Long subscribeId;   // 어떤 구독에 대한 환불인지
  private final ReasonCode reason;  // 사유 (USER_REQUEST, CONTRACT_CANCEL 등)
  private final ActorType actor;    // ADMIN 등
}

