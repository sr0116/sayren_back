package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;

public interface RefundService {

  // 환불 실행 (승인된 환불 요청 기반)
  void executeRefund(RefundRequest request, ReasonCode reasonCode);

  // 구독 취소 승인 시에 환불
  void executeRefundForSubscribe(Subscribe subscribe, ReasonCode reasonCode);
  // 필요시 환불 취소/롤백도 여기서 관리
  void cancelRefund(Long refundId);
}
