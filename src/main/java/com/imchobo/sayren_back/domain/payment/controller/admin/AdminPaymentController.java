package com.imchobo.sayren_back.domain.payment.controller.admin;

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
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@Log4j2
public class AdminPaymentController {

  private final PaymentService paymentService;
  // 관리자 전용 전체 결제 목록
  @GetMapping
  public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
    return ResponseEntity.ok(paymentService.getAllForAdmin());
  }
}
