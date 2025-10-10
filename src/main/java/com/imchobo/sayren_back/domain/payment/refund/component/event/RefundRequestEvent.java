package com.imchobo.sayren_back.domain.payment.refund.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class RefundRequestEvent   {
  private final Long orderItemId;
  private final Long subscribeId;   // 어떤 구독에 대한 환불인지
  private final RefundRequestStatus status; // 환불 요청 상태
  private final ReasonCode reason;  // 사유 (USER_REQUEST, CONTRACT_CANCEL 등)
  private final ActorType actor;    // ADMIN 등

}

