package com.imchobo.sayren_back.domain.payment.refund.repository;


import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {


}
