package com.imchobo.sayren_back.domain.payment.repository;


import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


  @Query("SELECT DISTINCT p FROM Payment p " +
          "JOIN FETCH p.orderItem oi " +
          "JOIN FETCH oi.order o " +
          "JOIN FETCH oi.orderPlan op " +
          "WHERE p.id = :paymentId")
  Optional<Payment> findWithOrderAndPlan(Long paymentId);

  // 결제 상세 (Order + Items + Plan까지 ) -> 나중에 서비스 코드 findWithOrderAndPlan 대신에 이걸로 수정
  @EntityGraph(attributePaths = {
          "orderItem",
          "orderItem.order",
          "orderItem.orderPlan"
  })
  Optional<Payment> findById(Long paymentId);
 // 일단 환불에서 사용중
  Optional<Payment> findByOrderItem(OrderItem orderItem);

  // 기본 CRUD, 결제 상태로 조회
  List<Payment> findByPaymentStatus(PaymentStatus status);

  // 포트원 검증용
  Optional<Payment> findByMerchantUid(String merchantUid);

  // 검증 조회용
  boolean existsByMerchantUid(String merchantUid);

  // 정렬 조회
  List<Payment> findAllByOrderByIdDesc();
}
