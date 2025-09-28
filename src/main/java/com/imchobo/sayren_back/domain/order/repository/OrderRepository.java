package com.imchobo.sayren_back.domain.order.repository;

import com.imchobo.sayren_back.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
  // 특정 회원의 주문 목록 조회 (최신순)
  List<Order> findByMemberIdOrderByIdDesc(Long memberId);
}
