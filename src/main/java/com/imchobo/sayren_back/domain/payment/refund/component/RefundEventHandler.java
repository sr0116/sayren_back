package com.imchobo.sayren_back.domain.payment.refund.component;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.component.event.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundRequestEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundService;
import com.imchobo.sayren_back.domain.subscribe.component.SubscribeCancelHandler;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
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
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class RefundEventHandler {

  private final RefundService refundService;
  private final SubscribeRepository subscribeRepository;
  private final SubscribeRoundRepository subscribeRoundRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final OrderItemRepository orderItemRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final SubscribeCancelHandler subscribeCancelHandler;

  // 관리자가 환불 승인/ 거절/ 취소 등 시에 호출됨
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onRefundRequestChanged(RefundRequestEvent event) {
    // 일반 사용자시
    if (event.getActor() == ActorType.USER) {
      log.debug("[SKIP] 사용자 환불 요청 이벤트 감지 → 승인 로직 생략");
      return;
    }
    Long subscribeId = event.getSubscribeId();
    Long orderItemId = event.getOrderItemId();
    RefundRequestStatus status = event.getStatus();

    log.info("[EVENT] RefundRequestEvent 수신 → orderItemId={}, subscribeId={}, status={}",
            orderItemId, subscribeId, status);

    // 거절 또는 취소 처리
    if (status == RefundRequestStatus.REJECTED || status == RefundRequestStatus.CANCELED) {
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(
              orderItemRepository.findById(orderItemId)
                      .orElseThrow(() -> new RuntimeException("OrderItem 없음: " + orderItemId))
      ).ifPresent(req -> {
        req.setStatus(status);
        refundRequestRepository.saveAndFlush(req);
        log.info("환불 요청 거절/취소 반영 완료 → refundRequestId={}, status={}", req.getId(), status);

        // 이벤트 재발행 (NotificationEventHandler가 AFTER_COMMIT에서 감지)
        eventPublisher.publishEvent(
                new RefundRequestEvent(orderItemId, subscribeId, status, req.getReasonCode(), ActorType.ADMIN)
        );
        log.debug("[RE-PUBLISH] 환불 요청 이벤트 재전파 완료 → status={}", status);
      });
      return;
    }

    // 일반 결제 환불 승인
    if (subscribeId == null) {
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(
              orderItemRepository.findById(orderItemId)
                      .orElseThrow(() -> new RuntimeException("OrderItem 없음: " + orderItemId))
      ).ifPresent(req -> {
        if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
          log.debug("[SKIP] 이미 APPROVED_WAITING_RETURN 상태 → 중복 재발행 생략");
          return;
        }

        if (req.getStatus() == RefundRequestStatus.PENDING) {
          req.setStatus(RefundRequestStatus.APPROVED_WAITING_RETURN);
          req.setReasonCode(event.getReason());
          refundRequestRepository.saveAndFlush(req);
          log.info("일반 결제 환불 승인 → PENDING → APPROVED_WAITING_RETURN");

          eventPublisher.publishEvent(
                  new RefundRequestEvent(orderItemId, null, RefundRequestStatus.APPROVED_WAITING_RETURN, req.getReasonCode(), ActorType.ADMIN)
          );
          log.debug("[RE-PUBLISH] 일반 결제 환불 승인 이벤트 재전파 완료");
        } else {
          log.warn("일반 결제 환불 승인 불가: refundRequestId={}, 현재 상태={}", req.getId(), req.getStatus());
        }
      });
      return;
    }
    // 구독 환불 승인
    subscribeRepository.findById(subscribeId).ifPresent(subscribe -> {
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
              .ifPresentOrElse(req -> {
                if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
                  log.debug("[SKIP] 이미 APPROVED_WAITING_RETURN 상태 → 중복 재발행 생략");
                  return;
                }
                if (req.getStatus() == RefundRequestStatus.PENDING) {
                  req.setStatus(RefundRequestStatus.APPROVED_WAITING_RETURN);
                  req.setReasonCode(event.getReason());
                  refundRequestRepository.saveAndFlush(req);
                  log.info("구독 환불 승인 → PENDING → APPROVED_WAITING_RETURN (회수 대기)");

                  // 구독 상태 변경은 아직 수행하지 않음 (환불 성공 후 실행)
                  eventPublisher.publishEvent(
                          new RefundRequestEvent(orderItemId, subscribeId,
                                  RefundRequestStatus.APPROVED_WAITING_RETURN,
                                  req.getReasonCode(),
                                  ActorType.ADMIN)
                  );
                  log.debug("[RE-PUBLISH] 구독 환불 승인 이벤트 재전파 완료");
                } else {
                  log.warn("구독 환불 승인 불가: refundRequestId={}, 현재 상태={}", req.getId(), req.getStatus());
                }
              }, () -> log.warn("해당 구독의 환불 요청 없음: subscribeId={}", subscribeId));
    });
  }

  // 배송 회수 완료 이벤트 → 자동 환불 처리
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDeliveryReturned(DeliveryStatusChangedEvent event) {
    // 배송 상태가 RETURNED가 아닐 경우 종료
    if (event.getStatus() != DeliveryStatus.RETURNED) return;

    log.info("배송 회수 완료 이벤트 감지 → 환불 자동 처리 시작: deliveryId={}, orderItemId={}",
            event.getDeliveryId(), event.getOrderItemId());

    try {
      // orderItemId로 RefundRequest 조회
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(
              orderItemRepository.findById(event.getOrderItemId())
                      .orElseThrow(() -> new RuntimeException("OrderItem 없음: " + event.getOrderItemId()))
      ).ifPresent(req -> {
        if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
          try {
            // 환불 실행
            refundService.executeRefund(
                    req,
                    req.getReasonCode() != null ? req.getReasonCode() : ReasonCode.AUTO_REFUND
            );

            // Detached 방지용 재조회 후 상태 확정
            RefundRequest managed = refundRequestRepository.findById(req.getId())
                    .orElseThrow(() -> new RuntimeException("RefundRequest 재조회 실패"));
            managed.setStatus(RefundRequestStatus.APPROVED);
            refundRequestRepository.saveAndFlush(managed);

            log.info("환불 성공: refundRequestId={}, orderItemId={}", req.getId(), event.getOrderItemId());
            subscribeRepository.findByOrderItem(req.getOrderItem())
                    .ifPresent(subscribe -> {
                      try {
                        // 구독 상태를 RETURNED_AND_CANCELED 로 변경
                        subscribeCancelHandler.handle(
                                subscribe,
                                RefundRequestStatus.APPROVED,
                                req.getReasonCode()
                        );
                        log.info("구독 회수 완료 → 상태 RETURNED_AND_CANCELED 전환 완료: subscribeId={}", subscribe.getId());
                      } catch (Exception e) {
                        log.error("구독 상태 전환 중 오류 발생: subscribeId={}, message={}", subscribe.getId(), e.getMessage());
                      }
                    });
          } catch (Exception e) {
            log.error("환불 처리 실패: refundRequestId={}, orderItemId={}, error={}",
                    req.getId(), event.getOrderItemId(), e.getMessage(), e);
          }
        } else {
          log.warn("환불 요청이 승인 대기 상태가 아님 → refundRequestId={}, status={}",
                  req.getId(), req.getStatus());
        }
      });
    } catch (Exception e) {
      log.error("배송 회수 환불 처리 중 예외 발생: deliveryId={}, orderItemId={}, message={}",
              event.getDeliveryId(), event.getOrderItemId(), e.getMessage(), e);
    }
  }
  // 구독 회수 완료 되었을 때 환불 실행
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onDeliveryReturnedSubscribe(SubscribeStatusChangedEvent event) {
    // 구독 상태가 RETURNED_AND_CANCELED로 전환된 시점에만 실행
    if (event.getTransition() != SubscribeTransition.RETURNED_AND_CANCELED) return;

    Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
            .orElseThrow(() -> new RuntimeException("구독 없음: " + event.getSubscribeId()));

    refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
            .ifPresent(req -> {
              // 회수 대기 상태일 때만 환불 실행
              if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
                try {
                  log.info("회수 완료 이벤트 감지 → PortOne 환불 처리 시작: refundRequestId={}, subscribeId={}",
                          req.getId(), subscribe.getId());

                  // PortOne 환불 실행
                  refundService.executeRefundForSubscribe(subscribe, req);

                  // 구독 회차 상태 일괄 변경
                  List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
                  rounds.forEach(r -> r.setPayStatus(PaymentStatus.REFUNDED));
                  subscribeRoundRepository.saveAll(rounds);

                  // 환불 상태 확정 갱신
                  RefundRequest managed = refundRequestRepository.findById(req.getId())
                          .orElseThrow(() -> new RuntimeException("RefundRequest 재조회 실패"));
                  managed.setStatus(RefundRequestStatus.APPROVED);
                  refundRequestRepository.saveAndFlush(managed);

                  // 환불 성공 후 구독 상태 최종 변경
                  subscribeCancelHandler.handle(
                          subscribe,
                          RefundRequestStatus.APPROVED,
                          req.getReasonCode() != null ? req.getReasonCode() : ReasonCode.AUTO_REFUND
                  );

                  log.info("회수 완료 → 환불 및 구독 종료 처리 완료: refundRequestId={}, subscribeId={}, 상태=APPROVED_WAITING_RETURN→APPROVED",
                          req.getId(), subscribe.getId());

                } catch (Exception e) {
                  log.error("PortOne 환불 실패: refundRequestId={}, subscribeId={}, message={}",
                          req.getId(), subscribe.getId(), e.getMessage());
                }
              } else {
                log.warn("회수 완료 이벤트 무시: refundRequestId={}, 현재 상태={}", req.getId(), req.getStatus());
              }
            });
  }

}

