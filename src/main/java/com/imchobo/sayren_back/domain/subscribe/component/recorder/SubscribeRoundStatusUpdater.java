package com.imchobo.sayren_back.domain.subscribe.component.recorder;

import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Log4j2
public class SubscribeRoundStatusUpdater {

  private final SubscribeRoundRepository subscribeRoundRepository;
  private final PaymentRepository paymentRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePaymentStatusChanged(PaymentStatusChangedEvent event) {
    paymentRepository.findById(event.getPaymentId())
            .map(Payment::getSubscribeRound) // Payment → SubscribeRound
            .ifPresent(round -> {
              SubscribeRoundTransition transition = mapToRoundTransition(event.getTransition());

              round.setPayStatus(transition.getStatus());
              if (transition == SubscribeRoundTransition.PAY_SUCCESS) {
                round.setPaidDate(LocalDateTime.now());
              }

              subscribeRoundRepository.save(round);
              log.info("회차 상태 자동 변경 - roundId={}, status={}", round.getId(), transition.getStatus());
            });
  }


  private SubscribeRoundTransition mapToRoundTransition(PaymentTransition transition) {
    return switch (transition) {
      case COMPLETE -> SubscribeRoundTransition.PAY_SUCCESS;
      case FAIL_USER, FAIL_PAYMENT, FAIL_SYSTEM -> SubscribeRoundTransition.PAY_FAIL;
      case FAIL_TIMEOUT -> SubscribeRoundTransition.PAY_TIMEOUT;
      case REFUND, PARTIAL_REFUND -> SubscribeRoundTransition.CANCEL;
    };
  }
}



