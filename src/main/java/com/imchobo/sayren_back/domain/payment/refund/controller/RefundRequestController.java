package com.imchobo.sayren_back.domain.payment.refund.controller;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/refunds/requests")
@RequiredArgsConstructor
public class RefundRequestController {

  private final RefundRequestService refundRequestService;

  // 1. 사용자 환불 요청 생성
  @PostMapping
  public ResponseEntity<RefundRequestResponseDTO> createRefundRequest(
          @RequestBody RefundRequestDTO dto) {
    return ResponseEntity.ok(refundRequestService.createRefundRequest(dto));
  }

  // 2. 사용자 본인 요청 취소
  @PostMapping("/{id}/cancel")
  public ResponseEntity<Void> cancelRefundRequest(@PathVariable Long id) {
    refundRequestService.cancelRefundRequest(id);
    return ResponseEntity.ok().build();
  }

  // 3. 관리자 승인 / 거절
  @PostMapping("/{id}/process")
  public ResponseEntity<RefundRequestResponseDTO> processRefundRequest(
          @PathVariable Long id,
          @RequestParam RefundRequestStatus status,
          @RequestParam ReasonCode reasonCode) {
    return ResponseEntity.ok(refundRequestService.processRefundRequest(id, status, reasonCode));
  }

  // 4. 단일 조회
  @GetMapping("/{id}")
  public ResponseEntity<RefundRequestResponseDTO> getRefundRequest(@PathVariable Long id) {
    return ResponseEntity.ok(refundRequestService.getRefundRequest(id));
  }

  // 5. 로그인한 사용자 환불 내역
  @GetMapping("/me")
  public ResponseEntity<List<RefundRequestResponseDTO>> getMyRefundRequests() {
    return ResponseEntity.ok(refundRequestService.getMyRefundRequests());
  }

  // 6. 관리자: 특정 회원 환불 내역
  @GetMapping("/member/{memberId}")
  public ResponseEntity<List<RefundRequestResponseDTO>> getRefundRequestsByMember(@PathVariable Long memberId) {
    return ResponseEntity.ok(refundRequestService.getRefundRequestsByMember(memberId));
  }
}
