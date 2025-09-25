package com.imchobo.sayren_back.domain.delivery.orchestration;

import com.imchobo.sayren_back.domain.delivery.component.event.StatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * Payment → Delivery 오케스트레이션
 * - Payment = PAID → Delivery READY 생성
 */
@Component
@RequiredArgsConstructor
public class PaymentToDeliveryOrchestrator {

  private final DeliveryService deliveryService;

  @EventListener
  public void onPaymentStatusChanged(StatusChangedEvent<?> event) {
    // Payment 도메인 이벤트만 처리
    if (!"PAYMENT".equals(event.getAggregateType())) return;

    String newStatus = event.getNewStatus().name();

    // 결제 완료 → 배송 READY 생성
    if ("PAID".equals(newStatus)) {
      Long orderId = (Long) event.getMetadata().get("orderId");
      if (orderId != null) {
        deliveryService.createFromOrderId(orderId);
      }
    }

    // 추후: 결제 환불/실패 → 배송 취소 흐름도 확장 가능
  }
}