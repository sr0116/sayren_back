package com.imchobo.sayren_back.domain.payment.component.recorder;

import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.entity.PaymentHistory;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.repository.PaymentHistoryRepository;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Log4j2
public class PaymentHistoryRecorder {

  private final PaymentHistoryRepository paymentHistoryRepository;
  private final PaymentRepository paymentRepository;

  // 트랜잭션이 성공적으로 끝난 뒤에만 실행
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePaymentStatusChanged(PaymentStatusChangedEvent event) {
    try {
      Payment payment = paymentRepository.findById(event.getPaymentId())
              .orElseThrow(() -> new PaymentNotFoundException(event.getPaymentId()));

      // 최근 히스토리 상태 확인 (중복 방지)
      PaymentHistory lastHistory = paymentHistoryRepository.findTopByPaymentOrderByCreatedAtDesc(payment);

      if (lastHistory != null && lastHistory.getStatus() == payment.getPaymentStatus()) {
        log.warn("중복 이벤트 무시 - paymentId={}, status={}", payment.getId(), payment.getPaymentStatus());
        return;
      }

      // 새로운 히스토리 생성
      PaymentHistory history = PaymentHistory.builder()
              .payment(payment)
              .status(payment.getPaymentStatus())
              .reasonCode(event.getTransition().getReason()) // 전환 사유 코드 기록
              .build();

      paymentHistoryRepository.save(history);
      log.info("결제 이력 저장 완료 - paymentId={}, status={}", payment.getId(), payment.getPaymentStatus());

    } catch (Exception e) {
      // 이력 저장 실패해도 본 로직 영향 X, 로그만 남김
      log.error("결제 이력 저장 실패 - event={}, error={}", event, e.getMessage(), e);
    }
  }
}
