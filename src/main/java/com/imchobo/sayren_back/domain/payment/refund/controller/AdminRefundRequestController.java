package com.imchobo.sayren_back.domain.payment.refund.controller;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/admin/refunds/requests")
@RequiredArgsConstructor
public class AdminRefundRequestController {

  private final RefundRequestService refundRequestService;

  // 관리자: 환불 요청 승인/거절
  @PostMapping("/{id}/process")
  public ResponseEntity<RefundRequestResponseDTO> processRefundRequest(
          @PathVariable Long id,
          @RequestParam RefundRequestStatus status,
          @RequestParam ReasonCode reasonCode) {
    return ResponseEntity.ok(refundRequestService.processRefundRequest(id, status, reasonCode));
  }

  // 관리자: 전체 환불 요청 조회
  @GetMapping
  public ResponseEntity<List<RefundRequestResponseDTO>> getAllRequests() {
    return ResponseEntity.ok(refundRequestService.getAllRefundRequests());
  }

  @GetMapping("/member/{memberId}")
  public ResponseEntity<List<RefundRequestResponseDTO>> getRefundRequestsByMember(@PathVariable Long memberId) {
    return ResponseEntity.ok(refundRequestService.getRefundRequestsByMember(memberId));
  }
}
