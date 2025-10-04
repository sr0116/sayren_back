package com.imchobo.sayren_back.domain.subscribe.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundApprovedEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

  @Component
  @RequiredArgsConstructor
  @Log4j2
  public class SubscribeCancelHandler {

    private final SubscribeStatusChanger statusChanger;
    private final ApplicationEventPublisher eventPublisher;

    public void handle(Subscribe subscribe, RefundRequestStatus status, ReasonCode reasonCode) {
      switch (status) {
        case APPROVED -> {
          statusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_APPROVE, ActorType.ADMIN);
          eventPublisher.publishEvent(new RefundApprovedEvent(subscribe.getId(), reasonCode, ActorType.ADMIN));
          log.info("구독 취소 승인 완료: subscribeId={}", subscribe.getId());
        }
        case REJECTED -> {
          statusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_REJECT, ActorType.ADMIN);
          log.info("구독 취소 거절 처리: subscribeId={}", subscribe.getId());
        }
        default -> throw new IllegalArgumentException("처리할 수 없는 환불 요청 상태: " + status);
      }
    }
  }

