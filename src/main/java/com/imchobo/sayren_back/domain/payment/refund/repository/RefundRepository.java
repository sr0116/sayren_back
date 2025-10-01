package com.imchobo.sayren_back.domain.payment.refund.repository;


import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RefundRepository extends JpaRepository<Refund, Long> {

  Optional<Refund> findFirstByPaymentOrderByRegDateDesc(Payment payment);
}
