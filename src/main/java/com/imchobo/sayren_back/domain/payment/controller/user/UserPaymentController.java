package com.imchobo.sayren_back.domain.payment.controller.user;

import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/payments")
@RequiredArgsConstructor
@Log4j2
public class UserPaymentController {

  private final PaymentService paymentService;

  // 결제 준비
  @PostMapping("/prepare")
  public PaymentResponseDTO prepare(@RequestBody PaymentRequestDTO dto) {
    return paymentService.prepare(dto);
  }

  // 결제 완료시 상태 변경
  @PostMapping("/{paymentId}/complete")
  public PaymentResponseDTO complete(@PathVariable Long paymentId, @RequestParam("imp_uid") String impUid) {
    //
    return paymentService.complete(paymentId, impUid);
  }

  // 회차 결제 준비
  @PostMapping("/prepare/round/{subscribeRoundId}")
  public PaymentResponseDTO prepareForRound(@PathVariable Long subscribeRoundId) {
    return paymentService.prepareForRound(subscribeRoundId);
  }


  // 결제 내역 조회 (마이페이지)
  @GetMapping("/summaries")
  public ResponseEntity<List<PaymentSummaryDTO>> getRecentPayments() {
    return ResponseEntity.ok(paymentService.getSummaries());
  }

  // 사용자 본인 결제 내역 조회
  @GetMapping
  public ResponseEntity<List<PaymentResponseDTO>> getAll() {
    return ResponseEntity.ok(paymentService.getAll());
  }

  @GetMapping("/{paymentId}")
  public ResponseEntity<PaymentResponseDTO> getOne(@PathVariable Long paymentId) {
    return ResponseEntity.ok(paymentService.getOne(paymentId));
  }
}
