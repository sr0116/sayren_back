package com.imchobo.sayren_back.domain.payment.component;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeRoundStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.component.recorder.HistoryRecorder;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
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
public class StatusChanger {
  // 나중에 삭제
  private final PaymentRepository paymentRepository;
  private final SubscribeRepository subscribeRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final HistoryRecorder historyRecorder;
  // 이벤트 처리
  private final ApplicationEventPublisher eventPublisher;

  // 결제 상태 변경
  @Transactional
  public void changePayment(Payment payment, PaymentTransition transition, ActorType actor){
    payment.setPaymentStatus(transition.getStatus());
    paymentRepository.save(payment);

    // 로그 기록
    historyRecorder.recordPayment(payment, transition.getReason(), actor);

    eventPublisher.publishEvent(
            new PaymentStatusChangedEvent(payment.getId(), transition.getStatus(),  payment.getOrderItem().getId() )
    );
  }

  // 구독 상태 변경
  @Transactional
  public void changeSubscribe(Subscribe subscribe, SubscribeTransition transition, ActorType actor) {
    subscribe.setStatus(transition.getStatus());
    subscribeRepository.save(subscribe);
    // 로그 기록 (구독, 사유, 주체? 그 변경자)
    historyRecorder.recordSubscribe(subscribe, transition.getReason(), actor);
    eventPublisher.publishEvent(
            new SubscribeStatusChangedEvent(subscribe.getId(), transition.getStatus())
    );
  }
    // 구독 회차 상태 변경
     @Transactional
     public void changeSubscribeRound(SubscribeRound subscribeRound, SubscribeRoundTransition transition, ActorType actor) {
       subscribeRound.setPayStatus(transition.getStatus());
       subscribeRoundRepository.save(subscribeRound);


       eventPublisher.publishEvent(
               new SubscribeRoundStatusChangedEvent(subscribeRound.getId(), transition.getStatus())
       );
     }

  }

