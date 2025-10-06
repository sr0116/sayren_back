package com.imchobo.sayren_back.domain.payment.refund.component;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundService;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class DeliveryEventHandler {

  private final DeliveryItemRepository deliveryItemRepository;
  private final SubscribeRepository subscribeRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final RefundService refundService;
  private final SubscribeRoundRepository subscribeRoundRepository;

  // 10초마다 RETURNED 상태의 배송을 확인하여 자동 환불 수행
//  @Scheduled(fixedDelay = 10000)
  public void checkReturnedDeliveries() {
    try {
      processReturnedDeliveries();
    } catch (Exception e) {
      log.error("자동 환불 스케줄러 실행 중 예외 발생", e);
    }
  }

  // 회수 완료된 배송을 찾아 환불 처리 수행
  // 개별 환불은 별도 트랜잭션으로 실행
  @Transactional(readOnly = true)
  public void processReturnedDeliveries() {
    List<DeliveryItem> returnedItems = deliveryItemRepository.findByDelivery_Status(DeliveryStatus.RETURNED);

    if (returnedItems.isEmpty()) {
      log.debug("회수 완료된 배송 없음 (skip)");
      return;
    }

    log.info("회수 완료된 배송 감지됨 → 처리 예정 건수: {}", returnedItems.size());

    for (DeliveryItem item : returnedItems) {
      handleReturnedItem(item); // 개별 트랜잭션 단위로 처리
    }
  }

  // 단일 회수 아이템에 대해 환불 처리 수행
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void handleReturnedItem(DeliveryItem item) {
    var orderItem = item.getOrderItem();
    if (orderItem == null) {
      log.warn("orderItem이 null → 처리 불가");
      return;
    }

    Subscribe subscribe = subscribeRepository.findByOrderItem(orderItem);
    if (subscribe == null) {
      log.warn("구독 엔티티 없음: orderItemId={}", orderItem.getId());
      return;
    }

    refundRequestRepository.findFirstByOrderItemOrderByRegDateDesc(orderItem)
            .ifPresent(req -> {
              if (req.getStatus() != RefundRequestStatus.APPROVED_WAITING_RETURN) {
                log.debug("환불 요청 상태가 회수대기 아님 (현재 상태={}) → skip", req.getStatus());
                return;
              }

              try {
                // 실제 환불 실행
                refundService.executeRefundForSubscribe(subscribe, req);

                // 환불 요청 상태 변경
                req.setStatus(RefundRequestStatus.APPROVED);
                refundRequestRepository.saveAndFlush(req);

                // 회차별 결제 상태 변경
                List<SubscribeRound> rounds = subscribeRoundRepository.findBySubscribeId(subscribe.getId());
                rounds.forEach(r -> r.setPayStatus(PaymentStatus.REFUNDED));
                subscribeRoundRepository.saveAllAndFlush(rounds);

                log.info(
                        "자동 환불 완료 → refundRequestId={} / 상태: APPROVED_WAITING_RETURN → APPROVED / subscribeId={} / 회차 {}건 REFUNDED",
                        req.getId(), subscribe.getId(), rounds.size()
                );

              } catch (Exception e) {
                log.error("환불 실패: refundRequestId={}, subscribeId={}, message={}",
                        req.getId(), subscribe.getId(), e.getMessage(), e);
              }
            });
  }
}
