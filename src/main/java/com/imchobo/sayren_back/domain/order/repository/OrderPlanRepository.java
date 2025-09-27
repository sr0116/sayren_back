package com.imchobo.sayren_back.domain.order.repository;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import com.imchobo.sayren_back.domain.order.entity.OrderPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 이 인터페이스가 Repository(DAO) 역할을 한다고 스프링에 알려줌
public interface OrderPlanRepository extends JpaRepository<OrderPlan, Long> {
  // JpaRepository<OrderPlan, Long>
  //  첫 번째 제네릭은 엔티티 클래스(OrderPlan), 두 번째는 PK 타입(Long)
  /**
   * 특정 type(구매/렌탈) + month(개월 수) 조합이 이미 존재하는지 확인하는 메서드
   * type: OrderPlan 엔티티의 type 필드 (enum)
   * month: OrderPlan 엔티티의 month 필드 (int, nullable)
   * orderPlanRepository.existsByTypeAndMonth(OrderPlanType.RENTAL, 12)
   * type=RENTAL, month=12 인 요금제가 존재하면 true 반환
   */
  boolean existsByTypeAndMonth(OrderPlanType type, Integer month);
}
