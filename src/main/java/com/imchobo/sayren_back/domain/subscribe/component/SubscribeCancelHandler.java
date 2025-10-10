package com.imchobo.sayren_back.domain.subscribe.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
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
        case APPROVED_WAITING_RETURN -> {
          // 관리자 승인 → 회수 대기
          statusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_APPROVE_PENDING, ActorType.ADMIN);
          log.info("구독 환불 승인 (회수 대기) 처리 완료: subscribeId={}", subscribe.getId());
        }

        case APPROVED -> {
          // 회수 완료 → 환불 완료 및 구독 취소 확정
          statusChanger.changeSubscribe(subscribe, SubscribeTransition.RETURNED_AND_CANCELED, ActorType.SYSTEM);
          log.info("구독 환불 완료 (회수 완료): subscribeId={}", subscribe.getId());
        }

        case REJECTED -> {
          // 관리자 거절
          statusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_REJECT, ActorType.ADMIN);
          log.info("구독 환불 요청 거절: subscribeId={}", subscribe.getId());
        }

        case CANCELED -> {
          // 사용자가 환불 요청 자체를 취소 (별도 Transition 없음 → 요청 철회로 처리)
          statusChanger.changeSubscribe(subscribe, SubscribeTransition.REQUEST_CANCEL, ActorType.USER);
          log.info("사용자 환불 요청 취소 → subscribeId={}", subscribe.getId());
        }

        case PENDING -> {
          // 초기 요청 시
          statusChanger.changeSubscribe(subscribe, SubscribeTransition.REQUEST_CANCEL, ActorType.USER);
          log.info("사용자 환불 요청 등록: subscribeId={}", subscribe.getId());
        }

        default -> {
          log.warn("처리할 수 없는 환불 요청 상태: {}", status);
          throw new IllegalArgumentException("처리할 수 없는 환불 요청 상태: " + status);
        }
      }
    }
  }

