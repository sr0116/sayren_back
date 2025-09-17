package com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository;


import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRoundRepository extends JpaRepository<SubscribeRound, Long> {
  // 특정 구독에 연결된 모든 회차
//  List<SubscribePayment> findBySubscribe(Subscribe subscribe);
//
//  // 특정 결제 건과 연결된 회차 (거의 1:1 관계, Optional)
//  Optional<SubscribePayment> findByPayment(Payment payment);
//  // 상태별 전체 회차 조회 (스케줄러에서 활용)
//  List<SubscribePayment> findByPayStatus(PaymentStatus payStatus);
//  // 특정 구독 + 상태별 조회 (페이징)
//  Page<SubscribePayment> findBySubscribeAndPayStatus(Subscribe subscribe, PaymentStatus payStatus, Pageable pageable);
//


}