package com.imchobo.sayren_back.domain.payment.component_recorder;

import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@AllArgsConstructor
public class PaymentStatusChangedEvent {
  private final Long paymentId;
  private final PaymentStatus status;
}