package com.imchobo.sayren_back.domain.delivery.component;

import com.imchobo.sayren_back.domain.delivery.component.event.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
@RequiredArgsConstructor
public class DeliveryStatusChanger {

  private final DeliveryRepository deliveryRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void changeDeliveryStatus(
    Delivery delivery,
    DeliveryType type,
    DeliveryStatus next,
    OrderItem orderItem
  ) {
    DeliveryStatus old = delivery.getStatus();
    delivery.setStatus(next);
    deliveryRepository.saveAndFlush(delivery);

    log.info("[배송 상태 변경] {} → {} (deliveryId={})", old, next, delivery.getId());

    //  구독/알림 모듈로 이벤트 발행
    eventPublisher.publishEvent(
      new DeliveryStatusChangedEvent(
        delivery.getId(),
        next,
        type,
        orderItem.getId()
      )
    );
  }
}
