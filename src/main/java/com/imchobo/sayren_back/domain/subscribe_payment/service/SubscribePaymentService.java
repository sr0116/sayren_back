package com.imchobo.sayren_back.domain.subscribe_payment.service;

import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;

public interface SubscribePaymentService {

  void  generateRounds(SubscribeResponseDTO subscribe, Payment payment);
}
