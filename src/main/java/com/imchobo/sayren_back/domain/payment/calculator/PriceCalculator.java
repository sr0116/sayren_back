package com.imchobo.sayren_back.domain.payment.calculator;

import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.subscribe.dto.RentalPriceDTO;

public interface PriceCalculator {
  // 렌탈 가격 계산
  RentalPriceDTO calculate(Long productPrice, int months);

}
