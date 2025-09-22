package com.imchobo.sayren_back.domain.subscribe.service;

import com.imchobo.sayren_back.domain.address.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
