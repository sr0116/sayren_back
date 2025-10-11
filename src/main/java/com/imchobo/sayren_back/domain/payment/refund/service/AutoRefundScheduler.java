package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class AutoRefundScheduler {

  private final PaymentRepository paymentRepository;
  private final RefundRequestRepository refundRequestRepository;
  private final RefundService refundService;

  //  @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시 실행
//  @Scheduled(fixedRate = 30000)
  @Transactional
  public void checkAutoRefundTargets() {
    log.info("===== [AUTO REFUND] 자동 환불 점검 시작 =====");

    refundService.processAutoRefundBatch();

    log.info("===== [AUTO REFUND] 자동 환불 점검 종료 =====");
  }
}