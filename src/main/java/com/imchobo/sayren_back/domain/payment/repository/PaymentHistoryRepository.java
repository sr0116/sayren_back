package com.imchobo.sayren_back.domain.payment.repository;


import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.entity.PaymentHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {


  PaymentHistory findTopByPaymentOrderByCreatedAtDesc(Payment payment);
}
