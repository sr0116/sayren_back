package com.imchobo.sayren_back.domain.delivery.component.history;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryHistory;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 배송 상태 변경 이력 기록 담당
 */
@Component
@RequiredArgsConstructor
public class DeliveryHistoryRecorder implements HistoryRecorder<Delivery> {

  private final DeliveryHistoryRepository repo;

  @Override
  public void record(Delivery entity,
                     Enum<?> oldStatus,
                     Enum<?> newStatus,
                     ReasonCode reason,
                     ActorType actor) {
    String oldName = (oldStatus != null) ? oldStatus.name() : null;
    String newName = (newStatus != null) ? newStatus.name() : null;

    DeliveryHistory h = DeliveryHistory.of(
      entity.getId(),
      oldName,
      newName,
      reason,
      actor,
      LocalDateTime.now()
    );
    repo.save(h);
  }
}