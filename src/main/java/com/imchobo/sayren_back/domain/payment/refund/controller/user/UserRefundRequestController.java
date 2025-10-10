package com.imchobo.sayren_back.domain.payment.refund.controller.user;

import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/refunds/requests")
@RequiredArgsConstructor
public class UserRefundRequestController {

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

}
