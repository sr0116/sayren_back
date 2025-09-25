package com.imchobo.sayren_back.domain.order.repository;

import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  // 특정 주문의 아이템 목록 조회
  List<OrderItem> findByOrderId(Long orderId);

  boolean existsByOrderPlanId(Long orderPlanId);
}
