package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
  // JpaRepository 덕분에 기본 CRUD 메서드 제공됨
  // ex) save(), findById(), findAll(), deleteById()
}
