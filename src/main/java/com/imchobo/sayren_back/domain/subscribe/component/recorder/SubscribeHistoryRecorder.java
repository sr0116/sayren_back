package com.imchobo.sayren_back.domain.subscribe.component.recorder;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeHistoryRecorder {
// 이벤트 리스너
  private final SubscribeHistoryRepository subscribeHistoryRepository;
  private final SubscribeRepository subscribeRepository;
  // 구독 이력 기록 및 상태

  @EventListener
  public void handleSubscribeStatusChanged(SubscribeStatusChangedEvent event) {
    // 1) 구독 엔티티 조회 (FK 저장용)
    Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
            .orElseThrow(() -> new SubscribeNotFoundException(event.getSubscribeId()));

    // 2) 이력 엔티티 생성
    SubscribeHistory history = SubscribeHistory.builder()
            .subscribe(subscribe)
            .status(event.getTransition().getStatus())    // 상태
            .reasonCode(event.getTransition().getReason())// 변경 이유
            .build();

    // 3) 저장
    subscribeHistoryRepository.save(history);
  }
}
