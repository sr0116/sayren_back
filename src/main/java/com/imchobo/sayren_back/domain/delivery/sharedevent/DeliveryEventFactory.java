package com.imchobo.sayren_back.domain.delivery.sharedevent;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEventFactory {

  public DeliveryStatusChangedEvent toStatusChangedEvent(Delivery delivery) {
    return new DeliveryStatusChangedEvent(
      delivery.getId(),
      delivery.getStatus(),
      delivery.getType(),
      delivery.getMember().getId()
    );
  }
}

  // 필요하면 다른 이벤트도 확장 가능
  // public DeliveryCreatedEvent toCreatedEvent(Delivery delivery) { ... }

