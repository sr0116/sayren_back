package com.imchobo.sayren_back.domain.delivery.component.history;
import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;

public interface HistoryRecorder<T> {
  void record(T entity,
              Enum<?> oldStatus,
              Enum<?> newStatus,
              ReasonCode reason,
              ActorType actor);
}