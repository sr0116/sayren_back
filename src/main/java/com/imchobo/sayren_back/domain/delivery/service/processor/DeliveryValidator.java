package com.imchobo.sayren_back.domain.delivery.service.processor;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

/**
 * Delivery 상태 전환 검증기
 */
@Component
public class DeliveryValidator {

  public void ensureTransition(Delivery delivery, DeliveryStatus expected, DeliveryStatus next) {
    if (delivery.getStatus() != expected) {
      throw new IllegalStateException(
        "Invalid transition: " + delivery.getStatus() + " → " + next
      );
    }
  }
}
