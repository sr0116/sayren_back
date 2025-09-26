package com.imchobo.sayren_back.domain.delivery.component.event;
import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 모든 도메인 공용 상태 변경 이벤트
 * - Delivery, Order, Payment, Subscribe 등에서 공통으로 사용
 */
@Getter
@Builder
public class StatusChangedEvent<T> {

  private final String aggregateType;   // DELIVERY, ORDER, PAYMENT 등
  private final Long id;                // 엔티티 PK
  private final Enum<?> oldStatus;
  private final Enum<?> newStatus;
  private final ReasonCode reason;
  private final ActorType actor;
  private final LocalDateTime changedAt;
  private final T snapshot;             // 엔티티 스냅샷
  private final Map<String, Object> metadata; // 부가정보 (orderId 등)
}
