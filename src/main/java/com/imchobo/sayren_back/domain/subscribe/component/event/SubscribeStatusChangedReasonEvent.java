package com.imchobo.sayren_back.domain.subscribe.component.event;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeStatusChangedReasonEvent {

  private final Long subscribeId;               // 구독 ID
  private final SubscribeTransition transition; // 상태 전이 정보
  private final ReasonCode reasonCode;          // 명시적 사유 코드
  private final ActorType actor;                // USER / ADMIN / SYSTEM
}
