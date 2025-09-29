package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;

import java.util.List;

public interface RefundRequestService {

  // 사용자가 환불 / 취소 요청 생성
  RefundRequestResponseDTO createRefundRequest(RefundRequestDTO dto);

  // 사용자 환불 취소 (본인이 취소 요청 한걸 취소)
  void cancelRefundRequest(Long refundRequestId);

  // 관리자 환불 취소 요청 처리
  RefundRequestResponseDTO processRefundRequest(Long refundRequestId, RefundRequestStatus status, ReasonCode reasonCode);

  // 환불 요청 단건 조회
  RefundRequestResponseDTO getRefundRequest(Long refundRequestId);

  // 로그인한 사용자의 환불 요청 목록 조회
  List<RefundRequestResponseDTO> getMyRefundRequests();

  // 특정 회원의 환불 요청 목록 조회 (관리자용)
  List<RefundRequestResponseDTO> getRefundRequestsByMember(Long memberId);


}
