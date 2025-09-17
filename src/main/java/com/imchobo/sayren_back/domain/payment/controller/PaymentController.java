package com.imchobo.sayren_back.domain.payment.controller;

import com.imchobo.sayren_back.domain.common.config.ApiResponse;
import com.imchobo.sayren_back.domain.payment.dto.PaymentRequestDTO;
import com.imchobo.sayren_back.domain.payment.dto.PaymentResponseDTO;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

//  private final PaymentService paymentService;
//  private final PaymentRepository paymentRepository;
//  private final PaymentMapper paymentMapper;
//
//  // 결제 준비
//  @PostMapping("/prepare")
//  public ApiResponse<PaymentResponseDTO> prepare(@RequestBody PaymentRequestDTO dto) {
//    return paymentService.prepare(dto);
//  }
//
//  // 결제 완료시 상태 변경
//  @PostMapping("/{paymentId}/complete")
//  public ApiResponse<PaymentResponseDTO> complete(@PathVariable Long paymentId, @RequestParam("imp_uid") String impUid) {
//
//    return paymentService.complete(paymentId, impUid);
//  }
//// 결제 환불
//  @PostMapping("/{paymentId}/refund")
//  public ApiResponse<Void> refund(@PathVariable Long paymentId, @RequestParam(required = false) Long amount, @RequestParam String reason) {
//    return paymentService.refund(paymentId, amount, reason);
//  }
//
//  @GetMapping
//  public ApiResponse<List<PaymentResponseDTO> > getAll() {
//    return paymentService.getAll();
//  }
}
