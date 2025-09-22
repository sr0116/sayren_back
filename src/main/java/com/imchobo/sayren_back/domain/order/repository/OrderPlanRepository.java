package com.imchobo.sayren_back.domain.order.repository;

import com.imchobo.sayren_back.domain.order.entity.OrderPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPlanRepository extends JpaRepository<OrderPlan, Long> {
}
