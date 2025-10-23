package com.imchobo.sayren_back.domain.order.OrderPlan.repository;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderPlanRepository extends JpaRepository<OrderPlan, Long> {

  boolean existsByTypeAndMonth(OrderPlanType type, Integer month);

  Optional<OrderPlan> findByTypeAndMonth(OrderPlanType type, Integer month);
}
