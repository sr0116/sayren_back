package com.imchobo.sayren_back.domain.subscribe.service;

import com.imchobo.sayren_back.domain.address.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;

interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
}
