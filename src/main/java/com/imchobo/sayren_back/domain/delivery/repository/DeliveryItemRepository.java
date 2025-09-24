package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
  List<DeliveryItem> findByOrderItem_Order_Id(Long orderId);
}
