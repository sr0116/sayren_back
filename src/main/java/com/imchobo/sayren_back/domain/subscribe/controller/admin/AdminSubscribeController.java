package com.imchobo.sayren_back.domain.subscribe.controller.admin;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscribes")
@RequiredArgsConstructor
public class AdminSubscribeController {
  private final SubscribeService subscribeService;

  // 모든 구독 전체 조회
  // 모든 구독 전체 조회 (관리자)
  @GetMapping
  public ResponseEntity<List<SubscribeResponseDTO>> getAll() {
    return ResponseEntity.ok(subscribeService.getAllForAdmin());
  }

  //  취소 요청 승인/거절
  @PostMapping("/{id}/cancel")
  public ResponseEntity<String> processCancel( @PathVariable Long id, @RequestParam String status, @RequestParam String reasonCode) {
    RefundRequestStatus requestStatus = RefundRequestStatus.valueOf(status);
    ReasonCode code = ReasonCode.valueOf(reasonCode);
    subscribeService.processCancelRequest(id, requestStatus, code);
    return ResponseEntity.ok("구독 취소 요청이 처리되었습니다.");
  }

}
