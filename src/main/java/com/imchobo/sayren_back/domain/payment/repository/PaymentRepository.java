package com.imchobo.sayren_back.domain.payment.repository;


import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


//  @Query("SELECT DISTINCT p FROM Payment p " +
//          "JOIN FETCH p.order o " +
//          "JOIN FETCH o.orderItems oi " +
//          "JOIN FETCH oi.plan " +
//          "WHERE p.id = :paymentId")
//  Optional<Payment> findWithOrderAndPlan(Long paymentId);
//
//  // 결제 상세 (Order + Items + Plan까지 ) -> 나중에 서비스 코드 findWithOrderAndPlan 대신에 이걸로 수정
//  @EntityGraph(attributePaths = {
//          "order",
//          "order.orderItems",
//          "order.orderItems.plan"
//  })
//  Optional<Payment> findById(Long paymentId);
//
//  // 기본 CRUD, 결제 상태로 조회
//  List<Payment> findByPayStatus(PaymentStatus status);
//
//// 포트원 검증용
//  Optional<Payment> findByMerchantUid(String merchantUid);

}
