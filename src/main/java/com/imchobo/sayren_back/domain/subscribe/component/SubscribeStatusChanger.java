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
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class SubscribeStatusChanger {

  private final SubscribeRepository subscribeRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;

  // 이벤트 처리
  private final ApplicationEventPublisher eventPublisher;

  // 구독 상태 변경
  @Transactional
  public void changeSubscribe(Subscribe subscribe, SubscribeTransition transition, ActorType actor) {
    subscribe.setStatus(transition.getStatus());
    subscribeRepository.saveAndFlush(subscribe);

    eventPublisher.publishEvent(new SubscribeStatusChangedEvent( subscribe.getId(), transition, actor));
    log.debug("구독 상태 이벤트 발행 완료: subscribeId={}, transition={}", subscribe.getId(), transition); // 이후에 주석
  }

  // 구독 회차 상태 변경 (결제 상태)
  @Transactional
  public void changeSubscribeRound(SubscribeRound subscribeRound, SubscribeRoundTransition transition) {
    // 1) 회차 상태 변경
    subscribeRound.setPayStatus(transition.getStatus());
    subscribeRoundRepository.saveAndFlush(subscribeRound);

    //  이벤트 발행
    eventPublisher.publishEvent(
            new SubscribeRoundStatusChangedEvent(subscribeRound.getId(), transition));
  }
}

