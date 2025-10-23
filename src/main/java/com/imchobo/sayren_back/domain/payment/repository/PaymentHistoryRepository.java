package com.imchobo.sayren_back.domain.payment.repository;


import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {


  PaymentHistory findTopByPaymentOrderByRegDateDesc(Payment payment);

  @Modifying
  @Query("DELETE FROM PaymentHistory h WHERE h.payment = :payment")
  void deleteAllByPayment(@Param("payment") Payment payment);

}
