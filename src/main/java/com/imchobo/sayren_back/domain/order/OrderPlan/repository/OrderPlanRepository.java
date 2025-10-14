package com.imchobo.sayren_back.domain.order.OrderPlan.repository;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 이 인터페이스가 Repository(DAO) 역할을 한다고 스프링에 알려줌
public interface OrderPlanRepository extends JpaRepository<OrderPlan, Long> {
  // JpaRepository<OrderPlan, Long>
  //  첫 번째 제네릭은 엔티티 클래스(OrderPlan), 두 번째는 PK 타입(Long)

  boolean existsByTypeAndMonth(OrderPlanType type, Integer month);

  Optional<OrderPlan> findByTypeAndMonth(OrderPlanType type, Integer month);
}
