package com.imchobo.sayren_back.domain.subscribe.controller.user;


import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeHistoryResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeSummaryDTO;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.dto.SubscribeRoundResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/subscribes")
@RequiredArgsConstructor
public class UserSubscribeController {
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
  public ResponseEntity<String> cancel(@PathVariable Long id) {
    subscribeService.cancelSubscribe(id);
    return ResponseEntity.ok("구독 취소 요청 완료");
  }

  // 구독 상태 변경 이력 조회
  @GetMapping("/{id}/histories")
  public ResponseEntity<List<SubscribeHistoryResponseDTO>> getHistories(@PathVariable Long id) {
    return ResponseEntity.ok(subscribeService.getHistories(id));
  }
  // 구독 회차 리스트 조회
  @GetMapping("/{subscribeId}/rounds")
  public List<SubscribeRoundResponseDTO> getRoundBySubscribe(@PathVariable Long subscribeId) {
    return subscribeService.getRoundBySubscribe(subscribeId);
  }

  // 구독 회차 단일 상세 조회
  @GetMapping("/{subscribeId}/rounds/{roundNo}")
  public SubscribeRoundResponseDTO getRoundDetail(@PathVariable Long subscribeId, @PathVariable Integer roundNo) {
   return subscribeService.getRoundDetail(subscribeId, roundNo);
  }

  // 구독 삭제 (사용자 본인만 가능)
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteSubscribe(@PathVariable Long id) {
    subscribeService.deleteSubscribe(id);
    return ResponseEntity.ok("구독 내역이 삭제되었습니다.");
  }

}
