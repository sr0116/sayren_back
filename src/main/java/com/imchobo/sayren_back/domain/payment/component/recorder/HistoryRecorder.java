package com.imchobo.sayren_back.domain.payment.component.recorder;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.entity.PaymentHistory;
import com.imchobo.sayren_back.domain.payment.refund.entity.Refund;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRepository;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentHistoryRepository;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.entity.SubscribeHistory;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoryRecorder {
  // reason_code 공통 사용

  private final PaymentHistoryRepository paymentHistoryRepository;
  private final SubscribeHistoryRepository subscribeHistoryRepository;
  private final RefundRepository refundRepository;
  private final RefundRequestRepository refundRequestRepository;

  // 결제 이력 기록 및 상태
  public void recordPayment (Payment payment, ReasonCode reasonCode, ActorType actorType){
    PaymentHistory history = PaymentHistory.builder()
            .payment(payment)
            .status(payment.getPaymentStatus())
            .reasonCode(reasonCode)
            .actorType(actorType)
            .build();
    paymentHistoryRepository.save(history);
  }

  // 구독 이력 기록 및 상태
  public void recordSubscribe (Subscribe subscribe, ReasonCode reasonCode, ActorType changeBy) {
    SubscribeHistory history = SubscribeHistory.builder()
            .subscribe(subscribe)
            .status(subscribe.getStatus())
            .reasonCode(reasonCode)
            .changedBy(changeBy)
            .build();
    subscribeHistoryRepository.save(history);
  }

  //  환불 요청 기록 및 상태
  public void recordRefundRequest(RefundRequest refundRequest, ReasonCode reasonCode) {
    refundRequest.setReasonCode(reasonCode);
    refundRequestRepository.save(refundRequest);
  }

  // 환불 기록
  public void recordRefund(Refund refund, ReasonCode reasonCode, Long amount) {
    refund.setReasonCode(reasonCode);
    refund.setAmount(amount);
    refundRepository.save(refund);
  }



}
