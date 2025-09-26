package com.imchobo.sayren_back.domain.delivery.service.processor;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.component.event.StatusChangedEvent;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    ReasonCode reason,
    ActorType actor,
    Map<String, Object> metadata) {

    // 1. 상태 전환 검증
    validator.ensureTransition(delivery, expected, next);

    // 2. 상태 변경
    DeliveryStatus old = delivery.getStatus();
    delivery.setStatus(next);

    // 3. 공용 이벤트 발행
    StatusChangedEvent<Delivery> event = StatusChangedEvent.<Delivery>builder()
      .aggregateType("DELIVERY")
      .id(delivery.getId())
      .oldStatus(old)
      .newStatus(next)
      .reason(reason)
      .actor(actor)
      .changedAt(LocalDateTime.now())
      .snapshot(delivery)
      .metadata(metadata)
      .build();

    publisher.publishEvent(event);
  }
}
