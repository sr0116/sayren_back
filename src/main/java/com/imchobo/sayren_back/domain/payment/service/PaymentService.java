package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.common.config.ApiResponseEx;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
  // Api 로 실제 결제 연동까지
  // 결제 준비
  ApiResponseEx<PaymentResponseDTO> prepare(PaymentRequestDTO dto);
  // 결제 완료
  ApiResponseEx<PaymentResponseDTO> complete(Long paymentId, String impUid);
  // 이후 환불(나중에 refund에서 따로 처리 할 수도 있음
  ApiResponseEx<Void> refund(Long paymentId, Long amount, String reason);

  ApiResponseEx<List<PaymentResponseDTO>> getAll ();

}
