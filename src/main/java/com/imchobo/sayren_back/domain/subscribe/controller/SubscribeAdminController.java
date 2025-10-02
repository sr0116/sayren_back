package com.imchobo.sayren_back.domain.subscribe.controller;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeHistoryResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/admin/subscribes")
@RequiredArgsConstructor
public class SubscribeAdminController {
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
          @RequestParam boolean approved,
          @RequestParam ReasonCode reasonCode
  ) {
    subscribeService.processCancelRequest(id, approved, reasonCode);
    return ResponseEntity.noContent().build();
  }
}
