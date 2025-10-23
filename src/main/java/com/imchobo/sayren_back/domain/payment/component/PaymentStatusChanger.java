package com.imchobo.sayren_back.domain.payment.component;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.order.entity.Order;
import com.imchobo.sayren_back.domain.payment.component.event.PaymentStatusChangedEvent;
import com.imchobo.sayren_back.domain.payment.en.PaymentTransition;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeRoundStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.component.event.SubscribeStatusChangedEvent;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeRoundTransition;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeTransition;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentStatusChanger {

  private final PaymentRepository paymentRepository;
  // 이벤트 처리
  private final ApplicationEventPublisher eventPublisher;

  // 결제 상태 변경
  @Transactional
  public void changePayment(Payment payment, PaymentTransition transition,  Long orderItemId, ActorType actor){
//    paymentRepository.save(payment);
    if (payment.getPaymentStatus() == transition.getStatus()) {
      // 같은 상태일 경우 중복 이벤트 방지
      return;
    }
    payment.setPaymentStatus(transition.getStatus());
    // 즉시 flush 처리로 최신 상태 보장
    paymentRepository.saveAndFlush(payment);

    eventPublisher.publishEvent(
            new PaymentStatusChangedEvent(
                    payment.getId(),
                    transition,
                    orderItemId,
                    actor)
    );
  }
}

