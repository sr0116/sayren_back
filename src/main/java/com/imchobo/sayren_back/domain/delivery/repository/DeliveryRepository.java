package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

  // 회원별 배송 조회
  List<Delivery> findByMember_Id(Long memberId);

  // 주문 ID로 배송 존재 여부 확인
  boolean existsByDeliveryItems_OrderItem_Order_Id(Long orderId);

  // 주문 ID로 배송 조회
  List<Delivery> findByDeliveryItems_OrderItem_Order_Id(Long orderId);
}
