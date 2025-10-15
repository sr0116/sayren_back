package com.imchobo.sayren_back.domain.payment.service;

import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;

import java.util.List;

public interface PaymentService {

  // 결제 준비
  PaymentResponseDTO prepare(PaymentRequestDTO dto);

  // 결제 완료 검증
  PaymentResponseDTO complete(Long paymentId, String impUid);

  //  전체 결제 내용 조회 (최근순 - 본인 것만)
  PaymentResponseDTO getOne(Long paymentId);

  List<PaymentResponseDTO> getAll();

  // 사용자별 결제 요약 조회 (마이페이지)
  List<PaymentSummaryDTO> getSummaries();

  // 관리자용 전체 조회
  List<PaymentResponseDTO> getAllForAdmin();

  // 스케줄러 처리
  PaymentResponseDTO prepareForRound(Long subscribeRoundId);

  PaymentResponseDTO prepareForRound(SubscribeRound round);

  // 결제 내역 삭제
  void deletePayment(Long paymentId);


}
