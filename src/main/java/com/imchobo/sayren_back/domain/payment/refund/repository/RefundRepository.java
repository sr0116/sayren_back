package com.imchobo.sayren_back.domain.payment.refund.repository;


import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RefundRepository extends JpaRepository<Refund, Long> {


}
