package com.imchobo.sayren_back.domain.payment.refund.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefundCompletedEvent {
  private final Long orderItemId;
  private final Long subscribeId;
  private final Long refundId;      // 생성된 Refund PK
  private final ReasonCode reason;    // ADMIN 등
}

