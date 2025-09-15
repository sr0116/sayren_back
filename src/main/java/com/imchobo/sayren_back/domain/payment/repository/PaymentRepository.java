package com.imchobo.sayren_back.domain.payment.repository;


import com.imchobo.sayren_back.domain.exentity.MemberEx;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


  @Query("SELECT DISTINCT p FROM Payment p " +
          "JOIN FETCH p.order o " +
          "JOIN FETCH o.orderItems oi " +
          "JOIN FETCH oi.plan " +
          "WHERE p.paymentId = :paymentId")
  Optional<Payment> findWithOrderAndPlan(Long paymentId);

  // 특정 회원의 결제 내역 조회(pk로 조회)
  List<Payment> findByMember_MemberId(Long memberId);
  // Member entity 로 조회
  List<Payment> findByMember(MemberEx memberEx);

  // 특정 회원이랑 상태로 조회
  List<Payment> findByMemberAndPayStatus(MemberEx memberEx, PaymentStatus payStatus);

  // 기본 CRUD, 결제 상태로 조회
  List<Payment> findByPayStatus(PaymentStatus status);;

  Optional<Payment> findByMerchantUid(String merchantUid);

}
