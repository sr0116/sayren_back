package com.imchobo.sayren_back.domain.order.component;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.order.entity.OrderHistory;
import com.imchobo.sayren_back.domain.order.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoryRecorder {

  private final OrderHistoryRepository orderHistoryRepository;

  public void record(Order order, OrderStatus status, ActorType actor) {
    OrderHistory history = OrderHistory.builder()
      .order(order)
      .member(order.getMember())
      .status(status)
      .address(order.getAddress() != null ? order.getAddress().toString() : "")
      .changedBy(actor)
      .build();

    orderHistoryRepository.save(history);
  }
}
