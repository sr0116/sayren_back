package com.imchobo.sayren_back.domain.payment.repository;


import com.imchobo.sayren_back.domain.exentity.Member;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  // 특정 회원의 결제 내역 조회(pk로 조회)
  List<Payment> findByMember_MemberId(Long memberId);
  // Member entity 로 조회
  List<Payment> findByMember(Member member);

  // 특정 회원이랑 상태로 조회
  List<Payment> findByMemberAndPayStatus(Member member, PaymentStatus payStatus);

  // 기본 CRUD, 결제 상태로 조회
  List<Payment> findByPayStatus(PaymentStatus status);;

  Optional<Payment> findByMerchantUid(String merchantUid);

}
