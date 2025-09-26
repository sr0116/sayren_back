package com.imchobo.sayren_back.domain.delivery.service.processor;

import com.imchobo.sayren_back.domain.delivery.component.event.StatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Delivery 상태 전환 전체 플로우 관리
 * - 서비스 단에서 호출하여 상태 변경 + 이벤트 발행
 * - 별도 DeliveryHistoryRecorder 는 존재하지 않음 (테이블 정의서 기준)
 */
@Component
@RequiredArgsConstructor
public class DeliveryFlowOrchestrator {

  private final DeliveryValidator validator;
  private final ApplicationEventPublisher publisher;

  public void changeStatus(
    Delivery delivery,
    DeliveryStatus expected,
    DeliveryStatus next,
    Map<String, Object> metadata) {

    // 1. 상태 전환 검증
    validator.ensureTransition(delivery, expected, next);

    // 2. 상태 변경
    delivery.setStatus(next);

    // 3. 구독 연결용 orderItemId 추출 (첫 번째 아이템 기준)
    Long orderItemId = null;
    if (delivery.getDeliveryItems() != null && !delivery.getDeliveryItems().isEmpty()) {
      DeliveryItem firstItem = delivery.getDeliveryItems().get(0);
      if (firstItem.getOrderItem() != null) {
        orderItemId = firstItem.getOrderItem().getId();
      }
    }

    // 4. 심플 이벤트 발행
    StatusChangedEvent event = new StatusChangedEvent(
      delivery.getId(),
      orderItemId,
      next
    );

    publisher.publishEvent(event);
  }
}