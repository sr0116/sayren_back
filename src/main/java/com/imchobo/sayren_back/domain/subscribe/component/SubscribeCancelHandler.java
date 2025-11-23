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
        //  계약 만료(EXPIRED): 단순 종료
        if (reasonCode == ReasonCode.EXPIRED) {
          statusChanger.changeSubscribe(
                  subscribe,
                  SubscribeTransition.END,     // ENDED 상태
                  reasonCode,
                  ActorType.ADMIN
          );
          log.info("구독 계약 만료 처리 완료 → subscribeId={}, status=ENDED", subscribe.getId());
          return;
        }

        //  배송 전 취소 (CUSTOMER_CANCEL_BEFORE_DELIVERY or AUTO_REFUND)
        if (reasonCode == ReasonCode.CUSTOMER_CANCEL_BEFORE_DELIVERY
                || reasonCode == ReasonCode.AUTO_REFUND
                || reasonCode == ReasonCode.NONE) {
          statusChanger.changeSubscribe(
                  subscribe,
                  SubscribeTransition.END,     // ENDED 상태
                  reasonCode,
                  ActorType.SYSTEM
          );
          log.info("배송 전 전액 환불 처리 완료 → subscribeId={}, status=ENDED", subscribe.getId());
          return;
        }

        //  일반 회수 완료 환불 → 계약 해지 처리
        statusChanger.changeSubscribe(
                subscribe,
                SubscribeTransition.RETURNED_AND_CANCELED,
                ActorType.SYSTEM
        );
        log.info("구독 환불 완료 (회수 완료): subscribeId={}", subscribe.getId());
      }

      case REJECTED -> {
        // 관리자 거절
        statusChanger.changeSubscribe(subscribe, SubscribeTransition.CANCEL_REJECT, ActorType.ADMIN);
        log.info("구독 환불 요청 거절: subscribeId={}", subscribe.getId());
      }

      case CANCELED -> {
        // 사용자가 환불 요청을 취소 (철회)
        statusChanger.changeSubscribe(subscribe, SubscribeTransition.REQUEST_CANCEL, ActorType.USER);
        log.info("사용자 환불 요청 취소 → subscribeId={}", subscribe.getId());
      }

      case PENDING -> {
        // 환불 요청 등록 (초기)
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
