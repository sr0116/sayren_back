package com.imchobo.sayren_back.domain.payment.component.recorder;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.entity.PaymentHistory;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.repository.PaymentHistoryRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHistoryRecorder {
  // 이벤트 리스너
  private final PaymentHistoryRepository paymentHistoryRepository;
  private final PaymentRepository paymentRepository;

  // 결제 이력 기록 및 상태
  @EventListener
  public void handlePaymentStatusChanged(PaymentStatusChangedEvent event){
    Payment payment = paymentRepository.findById(event.getPaymentId())
            .orElseThrow(() -> new PaymentNotFoundException(event.getPaymentId()));

    // 히스토리 생성
    PaymentHistory history = PaymentHistory.builder()
            .payment(payment)
            .status(payment.getPaymentStatus())
            .build();

    paymentHistoryRepository.save(history);
  }
}
