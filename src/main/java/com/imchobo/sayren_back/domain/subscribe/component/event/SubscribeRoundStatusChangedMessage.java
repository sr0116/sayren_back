package com.imchobo.sayren_back.domain.subscribe.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SubscribeRoundStatusChangedMessage {
  //  통합 이벤트 (알림이나 배송에서 사용 가능)

  private final Long subscribeRoundId;
  private final PaymentStatus status;     // 결제 상태
}
