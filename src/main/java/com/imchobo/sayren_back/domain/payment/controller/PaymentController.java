package com.imchobo.sayren_back.domain.payment.controller;

import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentSummaryDTO;
import com.imchobo.sayren_back.domain.payment.mapper.PaymentMapper;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
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
public class PaymentController {

  private final PaymentService paymentService;
  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;

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

  // 결제 내역 조회 (마이페이지)
  @GetMapping("/summaries")
  public ResponseEntity<List<PaymentSummaryDTO>> getRecentPayments() {
    return ResponseEntity.ok(paymentService.getSummaries());
  }

  @GetMapping
  public ResponseEntity<List<PaymentResponseDTO>> getAll() {
    return ResponseEntity.ok(paymentService.getAll());
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
    return ResponseEntity.ok(paymentService.getAllForAdmin());
  }
}
