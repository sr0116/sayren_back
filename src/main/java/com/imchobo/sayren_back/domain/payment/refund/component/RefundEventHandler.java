package com.imchobo.sayren_back.domain.payment.refund.component;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.component.event.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.refund.component.event.RefundApprovedEvent;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundService;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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
  private final PaymentRepository paymentRepository;
  private final OrderItemRepository orderItemRepository;
  private final DeliveryRepository deliveryRepository;

  // 관리자가 환불 승인 시에 호출됨
  @EventListener
  @Transactional
  public void onRefundApproved(RefundApprovedEvent event) {

    // 일반 결제 환불은 구독 ID가 없으므로 null 방어 필요
    if (event.getSubscribeId() == null) {
      log.info("일반 결제 환불 승인 이벤트 수신 → 구독 환불 처리 생략");
      return;
    }
    // 구독 환불 승인 로직 (구독 ID 존재 시에만 실행)
    subscribeRepository.findById(event.getSubscribeId())
            .ifPresentOrElse(subscribe -> {
              refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
                      .ifPresentOrElse(req -> {
                        if (req.getStatus() == RefundRequestStatus.PENDING) {
                          req.setStatus(RefundRequestStatus.APPROVED_WAITING_RETURN);
                          req.setReasonCode(event.getReason());
                          refundRequestRepository.save(req);
                          log.info("환불 요청 상태 변경 완료: refundRequestId={}, 상태=PENDING→APPROVED_WAITING_RETURN", req.getId());
                        } else {
                          log.warn("환불 요청 상태 변경 불가: refundRequestId={}, 현재 상태={}", req.getId(), req.getStatus());
                        }
                      }, () -> log.warn("해당 구독에 대한 환불 요청 없음: subscribeId={}", event.getSubscribeId()));
            }, () -> log.warn("구독 엔티티 없음: subscribeId={}", event.getSubscribeId()));
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


  // 회수 완료 되었을 때 환불 실행
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onDeliveryReturnedSubscribe(SubscribeStatusChangedEvent event) {
    if (event.getTransition() == SubscribeTransition.RETURNED_AND_CANCELED) {
      Subscribe subscribe = subscribeRepository.findById(event.getSubscribeId())
              .orElseThrow(() -> new RuntimeException("구독 없음: " + event.getSubscribeId()));

      refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(subscribe.getOrderItem())
              .ifPresent(req -> {
                if (req.getStatus() == RefundRequestStatus.APPROVED_WAITING_RETURN) {
                  try {
                    refundService.executeRefundForSubscribe(subscribe, req);

                    // 구독 회차 상태 일관 변경
                    List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
                    rounds.forEach(r -> r.setPayStatus(PaymentStatus.REFUNDED)); // 일단 환불 상태로 변경하고 이넘 추가 고려
                    subscribeRoundRepository.saveAll(rounds);

                    // Detached 방지용으로 재조회
                    RefundRequest managed = refundRequestRepository.findById(req.getId())
                            .orElseThrow(() -> new RuntimeException("RefundRequest 재조회 실패"));
                    managed.setStatus(RefundRequestStatus.APPROVED);
                    refundRequestRepository.saveAndFlush(managed);

                    log.info("회수 완료 → 환불 성공: refundRequestId={}, 상태=APPROVED_WAITING_RETURN→APPROVED",
                            req.getId());
                  } catch (Exception e) {
                    log.error("PortOne 환불 실패: refundRequestId={}, message={}", req.getId(), e.getMessage());
                    // 선택: req.setStatus(RefundRequestStatus.FAILED);
                  }
                } else {
                  log.warn("회수 완료 이벤트 무시: refundRequestId={}, 현재 상태={}", req.getId(), req.getStatus());
                }

              });
    }

  }
}

