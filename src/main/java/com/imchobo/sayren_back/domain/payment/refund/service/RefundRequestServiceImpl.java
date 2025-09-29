package com.imchobo.sayren_back.domain.payment.refund.service;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class RefundRequestServiceImpl implements RefundRequestService{

  @Override
  public RefundRequestResponseDTO createRefundRequest(RefundRequestDTO dto) {
    return null;
  }

  @Override
  public void cancelRefundRequest(Long refundRequestId) {

  }

  @Override
  public RefundRequestResponseDTO processRefundRequest(Long refundRequestId, RefundRequestStatus status, ReasonCode reasonCode) {
    return null;
  }

  @Override
  public RefundRequestResponseDTO getRefundRequest(Long refundRequestId) {
    return null;
  }

  @Override
  public List<RefundRequestResponseDTO> getMyRefundRequests() {
    return List.of();
  }

  @Override
  public List<RefundRequestResponseDTO> getRefundRequestsByMember(Long memberId) {
    return List.of();
  }
}
