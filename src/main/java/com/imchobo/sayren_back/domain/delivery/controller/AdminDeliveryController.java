package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.admin.DeliveryStatusChangeDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/deliveries")
@RequiredArgsConstructor
public class AdminDeliveryController {

  private final DeliveryService deliveryService;

  //  배송 전체 목록 조회 (관리자용)
  @GetMapping("/get-list")
  public ResponseEntity<?> getDeliveryList(PageRequestDTO pageRequestDTO) {
    return ResponseEntity.ok(deliveryService.getAllList(pageRequestDTO));
  }

  // 상태 변경 요청 (READY > SHIPPING > DELIVERED > RETURNED)
  @PostMapping("/changed-status")
  public ResponseEntity<?> changedDeliveryStatus(@RequestBody DeliveryStatusChangeDTO dto) {
    deliveryService.changedStatus(dto);
    return ResponseEntity.ok(Map.of("message", "배송 상태가 성공적으로 변경되었습니다."));
  }
}
