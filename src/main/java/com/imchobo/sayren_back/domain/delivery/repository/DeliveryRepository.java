package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
  List<Delivery> findByMember_Id(Long memberId);

  boolean existsByDeliveryItems_OrderItem_Id(Long orderItemId);

  List<Delivery> findByDeliveryItems_OrderItem_Order_Id(Long orderId);

  boolean existsByDeliveryItems_OrderItem(OrderItem deliveryItemsOrderItem);

  List<Delivery> findByStatus(DeliveryStatus status);
}
