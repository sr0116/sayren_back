package com.imchobo.sayren_back.domain.subscribe.component.event;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SubscribeStatusChangedMessage {
  // 통합 이벤트 도메인
  private final Long subscribeId;
  private final SubscribeStatus status;   // 단순 상태만
  private final ActorType actor;          // 변경 주체
}
