package com.imchobo.sayren_back.domain.subscribe_payment.repository;


import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe_payment.entity.SubscribePayment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribePaymentRepository extends JpaRepository<SubscribePayment, Long> {

  List<SubscribePayment> findBySubscribe(Subscribe subscribe);

  // 결제 id 기준
  List<SubscribePayment> findByPayment(Payment payment);

  List<SubscribePayment> findByPayStatus(PaymentStatus payStatus);

  List<SubscribePayment> findBySubscribeAndPayStatus(Subscribe subscribe, PaymentStatus payStatus, Pageable pageable);

  List<SubscribePayment> findBySubscribe_SubscribeId(Long subscribeId);


}
