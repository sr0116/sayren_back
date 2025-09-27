package com.imchobo.sayren_back.domain.subscribe.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class SubscribeExternalEventChanger {
// 배송 알림에서 사용
  private final SubscribeRepository subscribeRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final ApplicationEventPublisher eventPublisher;

  // 구독 상태 변경
//  @Transactional
//  public void changeSubscribe(Subscribe subscribe,
//                              SubscribeStatus newStatus,
//                              ActorType actor) {
//    subscribe.setStatus(newStatus);
//    subscribeRepository.save(subscribe);
//
//    // 이벤트 발행 (new 로 바로 생성)
//    eventPublisher.publishEvent(
//            new SubscribeStatusChangedMessage(subscribe.getId(), newStatus, actor)
//    );
//  }
//
//  // 구독 회차 결제 상태 변경
//  @Transactional
//  public void changeSubscribeRound(SubscribeRound subscribeRound,
//                                   PaymentStatus newStatus) {
//
//    subscribeRound.setPayStatus(newStatus);
//    subscribeRoundRepository.save(subscribeRound);
//
//    // 이벤트 발행 (new 로 바로 생성)
//    eventPublisher.publishEvent(
//            new SubscribeRoundStatusChangedMessage(subscribeRound.getId(), newStatus)
//    );
//  }
}