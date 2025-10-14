package com.imchobo.sayren_back.domain.subscribe.component;


import com.imchobo.sayren_back.domain.common.en.ActorType;

import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeRoundStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Log4j2
public class SubscribeStatusChanger {

  private final SubscribeRepository subscribeRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;

  // 이벤트 처리
  private final ApplicationEventPublisher eventPublisher;

  // 구독 상태 변경
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void changeSubscribe(Subscribe subscribe, SubscribeTransition transition, ActorType actor) {
    subscribe.setStatus(transition.getStatus());
    subscribeRepository.saveAndFlush(subscribe);

    // 커밋 후 리스너들이 감지할 수 있도록 동기 발행
    eventPublisher.publishEvent(new SubscribeStatusChangedEvent(subscribe.getId(), transition, actor));
    log.debug("[PUBLISH] 구독 상태 이벤트 발행 → subscribeId={}, transition={}", subscribe.getId(), transition);

  }

  // 구독 회차 상태 변경 (결제 상태)
  @Transactional
  public void changeSubscribeRound(SubscribeRound subscribeRound, SubscribeRoundTransition transition) {
    // 1) 회차 상태 변경
    subscribeRound.setPayStatus(transition.getStatus());
    subscribeRoundRepository.saveAndFlush(subscribeRound);

    eventPublisher.publishEvent(
            new SubscribeRoundStatusChangedEvent(subscribeRound.getId(), transition));
    log.debug("[EVENT] 구독 회차 상태 이벤트 발행 완료 → roundId={}, transition={}",
            subscribeRound.getId(), transition);
  }
}

