package com.imchobo.sayren_back.domain.delivery.orchestration;

import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;



 //Payment → Delivery 오케스트레이션
// Payment = PAID → Delivery READY 생성

@Component
@RequiredArgsConstructor
public class PaymentToDeliveryOrchestrator {

  private final DeliveryService deliveryService;

  @EventListener
  public void onPaymentStatusChanged(PaymentStatusChangedEvent event) {
    // 결제 완료 → 배송 READY 생성
    if (event.getStatus() == PaymentStatus.PAID) {
      Long orderItemId = event.getOrderItemId();
      if (orderItemId != null) {
        deliveryService.createFromOrderItemId(orderItemId);
      }
    }
  }
}