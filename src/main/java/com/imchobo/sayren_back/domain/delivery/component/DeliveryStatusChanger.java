package com.imchobo.sayren_back.domain.delivery.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.delivery.component.event.DeliveryStatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeliveryStatusChanger {

  private final DeliveryRepository deliveryRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void changeDeliveryStatus(Delivery delivery, DeliveryType type, DeliveryStatus status , OrderItem orderItem) {
    DeliveryStatus oldStatus = delivery.getStatus();

    // 상태 변경 및 DB 반영
    delivery.setStatus(status);
    deliveryRepository.saveAndFlush(delivery);

      eventPublisher.publishEvent(
              new DeliveryStatusChangedEvent(
                      delivery.getId(),
                      status,
                      type,
                      orderItem.getId()
              )

      );
    }
  }

