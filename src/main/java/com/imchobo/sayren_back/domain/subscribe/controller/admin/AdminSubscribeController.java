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
  @GetMapping
  public ResponseEntity<List<SubscribeResponseDTO>> getAll() {
    return ResponseEntity.ok(subscribeService.getAll());
  }

  //  취소 요청 승인/거절
  @PostMapping("/{id}/cancel")
  public ResponseEntity<Void> processCancel(
          @PathVariable Long id,
          @RequestParam RefundRequestStatus status,
          @RequestParam ReasonCode reasonCode
  ) {
    subscribeService.processCancelRequest(id, status, reasonCode);
    return ResponseEntity.noContent().build();
  }
}
