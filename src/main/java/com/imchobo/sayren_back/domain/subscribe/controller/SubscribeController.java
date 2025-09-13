package com.imchobo.sayren_back.domain.subscribe.controller;


import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeRequestDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscribes")
@RequiredArgsConstructor
public class SubscribeController {

  private final SubscribeService subscribeService;

  // 구독 신청
  @PostMapping
  public ResponseEntity<SubscribeResponseDTO> create(@RequestBody SubscribeRequestDTO dto) {
    SubscribeResponseDTO responseDTO = subscribeService.create(dto);
    return  ResponseEntity.ok(responseDTO);
  }


  // 구독 전체 조회
  @GetMapping
  public ResponseEntity<List<SubscribeResponseDTO>> getAll() {
    return ResponseEntity.ok(subscribeService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<SubscribeResponseDTO> getById(@PathVariable("id") Long subscribeId) {
    SubscribeResponseDTO dto = subscribeService.getById(subscribeId);
    return ResponseEntity.ok(dto);
  }

  // 구독 마이 페이지(요약) 조회
  @GetMapping("/summary")
  public ResponseEntity<List<SubscribeSummaryDTO>> getSummeryList() {
    return ResponseEntity.ok(subscribeService.getSummaryList());
  }

  // 구독 상태 변경
  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> updateStatus(
          @PathVariable("id") Long subscribeId,
          @RequestParam("status") SubscribeStatus status
          ) {
    subscribeService.updateStatus(subscribeId, status);
    return ResponseEntity.noContent().build();
  }
}
