package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
  // Api 로 실제 결제 연동까지
  // 결제 준비
  PaymentResponseDTO prepare(PaymentRequestDTO dto);

  // 결제 완료 검증
  PaymentResponseDTO complete(Long paymentId, String imUid);

  // 환불 처리
  void refund(Long paymentId, Long amount, String reason);

//  전체 결제 내영 조회 (최근순)
  List<PaymentResponseDTO> getAll();
}
