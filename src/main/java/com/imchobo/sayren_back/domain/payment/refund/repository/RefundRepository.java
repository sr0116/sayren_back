package com.imchobo.sayren_back.domain.payment.refund.repository;


import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface RefundRepository extends JpaRepository<Refund, Long> {

  Optional<Refund> findFirstByPaymentOrderByRegDateDesc(Payment payment);

  boolean existsByPayment(Payment payment);

  @Modifying
  @Query("DELETE FROM Refund r WHERE r.payment = :payment")
  void deleteAllByPayment(@Param("payment") Payment payment);
  // 환불 금액
  Optional<Refund> findTopByRefundRequestOrderByRegDateDesc(RefundRequest refundRequest);
}
