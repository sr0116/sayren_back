package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

  private final DeliveryService deliveryService;

  //  배송 생성
  @PostMapping
  public ResponseEntity<Long> createDelivery(@RequestBody DeliveryDTO dto) {
    return ResponseEntity.ok(deliveryService.createDelivery(dto));
  }

  //  배송 조회
  @GetMapping("/{id}")
  public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable Long id) {
    return ResponseEntity.ok(deliveryService.getDelivery(id));
  }

  // 배송 상태 변경
  @PutMapping("/{id}/status")
  public ResponseEntity<String> updateStatus(
    @PathVariable Long id,
    @RequestParam String status,
    @RequestParam(required = false) String trackingNo) {
    deliveryService.updateStatus(id, status, trackingNo);
    return ResponseEntity.ok("배송 상태가 변경되었습니다.");
  }
}
