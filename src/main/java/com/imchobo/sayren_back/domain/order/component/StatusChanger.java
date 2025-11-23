package com.imchobo.sayren_back.domain.order.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import com.imchobo.sayren_back.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusChanger {

  private final HistoryRecorder historyRecorder;

  public void change(Order order, OrderStatus newStatus, ActorType actor) {
    order.setStatus(newStatus);
    historyRecorder.record(order, newStatus, actor);
    // 필요시 이벤트 발행까지 여기서 묶을 수 있음
  }
}