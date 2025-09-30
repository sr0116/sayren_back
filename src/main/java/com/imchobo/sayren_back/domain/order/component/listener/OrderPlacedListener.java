package com.imchobo.sayren_back.domain.order.component.listener;

import com.imchobo.sayren_back.domain.delivery.orchestration.DeliveryCreationService;
import com.imchobo.sayren_back.domain.order.component.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


  //주문 커밋 완료 후 자동으로 배송을 생성하는 리스너

@Component
@RequiredArgsConstructor
@Log4j2
public class OrderPlacedListener {

  private final DeliveryCreationService deliveryCreationService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(OrderPlacedEvent event) {
    log.info("[OrderPlacedListener] AFTER_COMMIT orderId={}", event.getOrderId());

    deliveryCreationService.createIfAbsent(event.getOrderId());
  }
}