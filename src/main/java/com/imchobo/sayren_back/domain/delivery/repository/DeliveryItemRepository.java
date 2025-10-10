package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
  List<DeliveryItem> findByOrderItem_Order_Id(Long orderId);

  List<DeliveryItem> findByOrderItem(OrderItem orderItem);

  Optional<DeliveryItem> findFirstByOrderItem(OrderItem orderItem);

  Optional<DeliveryItem> findTopByOrderItemOrderByDelivery_RegDate_Desc(OrderItem orderItem);

  DeliveryRepository findByDelivery(Delivery delivery);

  List<DeliveryItem> findByDelivery_Status(DeliveryStatus status);
}
