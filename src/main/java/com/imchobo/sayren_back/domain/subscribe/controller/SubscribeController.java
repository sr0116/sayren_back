package com.imchobo.sayren_back.domain.subscribe.controller;


import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeHistoryResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/subscribes")
@RequiredArgsConstructor
public class SubscribeController {
  private final SubscribeService subscribeService;

  // 마이페이지용 요약 구독 내역 (본인)
  @GetMapping
  public ResponseEntity<List<SubscribeSummaryDTO>> getMySubscribes () {
    return ResponseEntity.ok(subscribeService.getSummaryList());
  }

  // 구독 단건 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<SubscribeResponseDTO> getSubscribe(@PathVariable Long id) {
    return ResponseEntity.ok(subscribeService.getSubscribe(id));
  }

  // 구독 취소 요청(사용자)
  @PostMapping("/{id}/cancel")
  public ResponseEntity<Void> cancel(@PathVariable Long id) {
    subscribeService.cancelSubscribe(id);
    return ResponseEntity.noContent().build();
  }
  // 4) 구독 상태 변경 이력 조회
  @GetMapping("/{id}/histories")
  public ResponseEntity<List<SubscribeHistoryResponseDTO>> getHistories(@PathVariable Long id) {
    return ResponseEntity.ok(subscribeService.getHistories(id));
  }
}
