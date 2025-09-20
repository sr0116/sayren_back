package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
}
