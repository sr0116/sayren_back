package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.entity.Payment;

import java.util.List;

public interface PaymentService {
  // Api 로 실제 결제 연동까지
  // 결제 준비
  PaymentResponseDTO prepare(PaymentRequestDTO dto);

  // 결제 완료 검증
  PaymentResponseDTO complete(Long paymentId, String impUid);

  // 환불 처리
  void refund(Long paymentId, Long amount, String reason);

//  전체 결제 내용 조회 (최근순 - 본인 것만)
  List<PaymentResponseDTO> getAll();

  // 사용자별 결제 요약 조회 (마이페이지)
  List<PaymentSummaryDTO> getSummaries();

  // 관리자용 전체 조회
  List<PaymentResponseDTO> getAllForAdmin();

}
