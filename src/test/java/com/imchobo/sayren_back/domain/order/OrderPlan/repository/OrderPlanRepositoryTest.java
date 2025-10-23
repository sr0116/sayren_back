package com.imchobo.sayren_back.domain.order.OrderPlan.repository;

import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderPlanRepositoryTest {
  @Autowired
  private OrderPlanRepository orderPlanRepository;

  @Test
  @DisplayName("플랜 생성")
  void addOrderPlan() {
    orderPlanRepository.save(OrderPlan.builder()
        .type(OrderPlanType.PURCHASE)
      .build());
  }

  @Test
  @DisplayName("플랜 삭제")
  void deleteOrderPlan() {
    orderPlanRepository.deleteById(4L);
  }
}