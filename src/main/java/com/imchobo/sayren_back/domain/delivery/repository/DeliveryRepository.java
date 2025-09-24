package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
  // 연관 엔티티의 FK로 검색할 때는 언더스코어 표기
  List<Delivery> findByMember_Id(Long memberId);
}
