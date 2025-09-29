package com.imchobo.sayren_back.domain.payment.calculator;

import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;

public interface  RefundCalculator {
// 환불 정책 계산
  Long calculateRefundAmount(Payment payment, OrderItem orderItem, RefundRequest request);

}
