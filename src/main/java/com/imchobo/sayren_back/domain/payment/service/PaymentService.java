package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.common.config.ApiResponse;
import com.imchobo.sayren_back.domain.exentity.Member;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.entity.Payment;

public interface PaymentService {
  // Api 로 실제 결제 연동까지
  // 결제 준비
  ApiResponse<PaymentResponseDTO> prepare(PaymentRequestDTO dto);
  // 결제 완료
  ApiResponse<PaymentResponseDTO> complete(Long paymentId, String impUid);
  // 이후 환불(나중에 refund에서 따로 처리 할 수도 있음
  ApiResponse<Void>  refund(Long paymentId, Long amount, String reason);

}
