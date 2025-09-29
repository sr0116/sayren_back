package com.imchobo.sayren_back.domain.Refund;


import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundRequestService;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class RefundRequestServiceTest {

  @Autowired
  private RefundRequestService refundRequestService;

  @Autowired
  private RefundRequestRepository refundRequestRepository;

  @Autowired
  private PaymentRepository paymentRepository;



}
