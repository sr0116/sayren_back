package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
  List<Delivery> findByMemberId(Long memberId);
}
