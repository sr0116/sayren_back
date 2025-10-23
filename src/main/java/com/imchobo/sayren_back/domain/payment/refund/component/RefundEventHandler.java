package com.imchobo.sayren_back.domain.payment.refund.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.component.event.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundRequestEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundService;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
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

import java.time.LocalDate;
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
  private final PaymentRepository paymentRepository;

  /**
   * 관리자가 환불 요청 승인/거절/취소할 때 호출됨
   * - APPROVED_WAITING_RETURN 상태로 변경 후 이벤트 재발행
   * - 승인 후 실제 환불은 '배송 회수 완료' 이벤트에서 수행
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onRefundRequestChanged(RefundRequestEvent event) {

    if (event.getActor() == ActorType.USER) {
      log.debug("[SKIP] 사용자 요청 이벤트 → 관리자 승인 로직 생략");
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

        // 재발행 → 알림 처리 핸들러에서 후속 작업
        eventPublisher.publishEvent(
                new RefundRequestEvent(orderItemId, subscribeId, status, req.getReasonCode(), ActorType.ADMIN)
        );
      });
      return;
    }

    // 일반 결제 환불 승인 처리
    if (subscribeId == null) {
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(
              orderItemRepository.findById(orderItemId)
                      .orElseThrow(() -> new RuntimeException("OrderItem 없음: " + orderItemId))
      ).ifPresent(req -> {
        if (req.getStatus() == RefundRequestStatus.PENDING) {
          req.setStatus(RefundRequestStatus.APPROVED_WAITING_RETURN);
          req.setReasonCode(event.getReason());
          refundRequestRepository.saveAndFlush(req);
          log.info("일반 결제 환불 승인 완료 → 상태: PENDING → APPROVED_WAITING_RETURN");

          eventPublisher.publishEvent(
                  new RefundRequestEvent(orderItemId, null, RefundRequestStatus.APPROVED_WAITING_RETURN,
                          req.getReasonCode(), ActorType.ADMIN)
          );
        }
      });
      return;
    }

    // 구독 결제 환불 승인 처리
    subscribeRepository.findById(subscribeId).ifPresent(subscribe -> {
      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
              .ifPresent(req -> {
                if (req.getStatus() == RefundRequestStatus.PENDING) {
                  req.setStatus(RefundRequestStatus.APPROVED_WAITING_RETURN);
                  req.setReasonCode(event.getReason());
                  refundRequestRepository.saveAndFlush(req);
                  log.info("구독 결제 환불 승인 완료 → PENDING → APPROVED_WAITING_RETURN");

                  eventPublisher.publishEvent(
                          new RefundRequestEvent(orderItemId, subscribeId,
                                  RefundRequestStatus.APPROVED_WAITING_RETURN,
                                  req.getReasonCode(),
                                  ActorType.ADMIN)
                  );
                }
              });
    });
  }

  /**
   * 배송 회수 완료 이벤트 → 자동 환불 처리 (일반 결제)
   * - APPROVED_WAITING_RETURN 상태일 경우 자동 실행
   * - RefundService.executeRefund() 내부에서 전체/부분 구분 처리
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDeliveryReturned(DeliveryStatusChangedEvent event) {
    if (event.getStatus() != DeliveryStatus.RETURNED) return;

    log.info("배송 회수 완료 이벤트 감지 → 자동 환불 처리 시작: deliveryId={}, orderItemId={}",
            event.getDeliveryId(), event.getOrderItemId());

    refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(
            orderItemRepository.findById(event.getOrderItemId())
                    .orElseThrow(() -> new RuntimeException("OrderItem 없음: " + event.getOrderItemId()))
    ).ifPresent(req -> {
      if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
        try {
          refundService.executeRefund(req,
                  req.getReasonCode() != null ? req.getReasonCode() : ReasonCode.AUTO_REFUND);

          // 상태 확정
          RefundRequest managed = refundRequestRepository.findById(req.getId())
                  .orElseThrow(() -> new RuntimeException("RefundRequest 재조회 실패"));
          managed.setStatus(RefundRequestStatus.APPROVED);
          refundRequestRepository.saveAndFlush(managed);

          log.info("일반 결제 환불 완료 → refundRequestId={}, orderItemId={}",
                  req.getId(), event.getOrderItemId());

          // 구독이 연결된 경우, 상태 변경 처리
          subscribeRepository.findByOrderItem(req.getOrderItem())
                  .ifPresent(subscribe -> {
                    try {
                      subscribeCancelHandler.handle(
                              subscribe,
                              RefundRequestStatus.APPROVED,
                              req.getReasonCode()
                      );
                    } catch (Exception e) {
                      log.error("구독 상태 전환 중 오류 발생: {}", e.getMessage());
                    }
                  });

        } catch (Exception e) {
          log.error("환불 처리 실패: refundRequestId={}, orderItemId={}, message={}",
                  req.getId(), event.getOrderItemId(), e.getMessage());
        }
      }
    });
  }

  /**
   * 구독 회수 완료 시 환불 실행
   * - RefundService.executeRefundForSubscribe() 내부에서 전체/부분 자동 판정
   * - 최신 Payment 상태를 기반으로 회차 결제 상태 업데이트
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onDeliveryReturnedSubscribe(SubscribeStatusChangedEvent event) {
    if (event.getTransition() != SubscribeTransition.RETURNED_AND_CANCELED) return;

    Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
            .orElseThrow(() -> new RuntimeException("구독 없음: " + event.getSubscribeId()));

    refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
            .ifPresent(req -> {
              if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
                try {
                  log.info("구독 회수 완료 이벤트 감지 → PortOne 환불 처리 시작: refundRequestId={}, subscribeId={}",
                          req.getId(), subscribe.getId());

                  // 실제 PG 환불 실행
                  refundService.executeRefundForSubscribe(subscribe, req);

                  // 결제 상태 최신화 (Payment 테이블에서 실제 상태 조회)
                  // 최신 결제 상태 조회 (Repository 사용)
                  PaymentStatus latestStatus = paymentRepository
                          .findTopByOrderItemOrderByIdDesc(subscribe.getOrderItem())
                          .map(Payment::getPaymentStatus)
                          .orElse(PaymentStatus.REFUNDED);


                  List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());

                  switch (latestStatus) {
                    case REFUNDED -> {
                      // 전체 환불 → 모든 회차 REFUNDED 처리
                      rounds.forEach(r -> r.setPayStatus(PaymentStatus.REFUNDED));
                      log.info("전체 환불 완료 → 모든 회차 REFUNDED 처리");
                    }
                    case PARTIAL_REFUNDED -> {
                      // 부분 환불 → 미래 회차만 CANCELED
                      for (SubscribeRound r : rounds) {
                        if (r.getDueDate() != null && r.getDueDate().isAfter(LocalDate.now())
                                && r.getPayStatus() != PaymentStatus.PAID) {
                          r.setPayStatus(PaymentStatus.CANCELED);
                        }
                      }
                      log.info("부분 환불 완료 → 미래 회차 CANCELED, 기존 회차 유지");
                    }
                    case CANCELED -> {
                      for (SubscribeRound r : rounds) {
                        if (r.getDueDate() != null && r.getDueDate().isAfter(LocalDate.now())) {
                          r.setPayStatus(PaymentStatus.CANCELED);
                        }
                      }
                      log.info("예약 결제 취소 처리 완료 → 미래 회차 CANCELED");
                    }
                    default -> log.info("기타 상태({}) → 회차 변경 없음", latestStatus);
                  }

                  subscribeRoundRepository.saveAll(rounds);

                  // 환불 요청 상태 확정
                  RefundRequest managed = refundRequestRepository.findById(req.getId())
                          .orElseThrow(() -> new RuntimeException("RefundRequest 재조회 실패"));
                  managed.setStatus(RefundRequestStatus.APPROVED);
                  refundRequestRepository.saveAndFlush(managed);

                  // 구독 상태 최종 종료
                  subscribeCancelHandler.handle(
                          subscribe,
                          RefundRequestStatus.APPROVED,
                          req.getReasonCode() != null ? req.getReasonCode() : ReasonCode.AUTO_REFUND
                  );

                  log.info("구독 회수 완료 → 환불/회차/구독 상태 최종 확정 완료");

                } catch (Exception e) {
                  log.error("구독 환불 처리 실패: refundRequestId={}, subscribeId={}, message={}",
                          req.getId(), subscribe.getId(), e.getMessage());
                }
              }
            });
  }
}
