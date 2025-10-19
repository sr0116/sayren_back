package com.imchobo.sayren_back.domain.subscribe.controller.admin;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscribes")
@RequiredArgsConstructor
@Log4j2
public class AdminSubscribeController {
  private final SubscribeService subscribeService;

  //  관리자: 전체 구독 목록 조회
  @GetMapping
  public ResponseEntity<List<SubscribeResponseDTO>> getAll() {
    List<SubscribeResponseDTO> list = subscribeService.getAllForAdmin();
    log.info("[ADMIN] 구독 목록 조회: {}건", list.size());
    return ResponseEntity.ok(list);
  }

  //  관리자: 취소 요청 승인 / 거절 처리
  @PostMapping("/{id}/cancel")
  public ResponseEntity<String> processCancel(
          @PathVariable Long id,
          @RequestParam String status,
          @RequestParam String reasonCode
  ) {
    RefundRequestStatus requestStatus = RefundRequestStatus.valueOf(status);
    ReasonCode code = ReasonCode.valueOf(reasonCode);
    subscribeService.processCancelRequest(id, requestStatus, code);
    return ResponseEntity.ok("구독 취소 요청이 처리되었습니다.");
  }
  @GetMapping("/debug")
  public ResponseEntity<?> debug() {
    List<SubscribeResponseDTO> list = subscribeService.getAllForAdmin();
    log.info(" [DEBUG] 컨트롤러 전달 DTO ={}", list.size());
    list.forEach(dto -> log.info("DTO={}", dto));
    return ResponseEntity.ok(list);
  }

}
