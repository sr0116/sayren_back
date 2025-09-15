package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")      // 배송 API 루트
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService; // 주입

    // 배송 생성(출고/반납 공통) : POST /api/deliveries
    @PostMapping
    public ResponseEntity<Long> createDelivery(@RequestBody DeliveryDTO dto) {
        // 요청 바디 → DTO → 서비스 저장
        Long id = deliveryService.createDelivery(dto);
        return ResponseEntity.ok(id); // 생성된 PK 반환
    }

    // 단건 조회 : GET /api/deliveries/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDelivery(id));
    }

    // 상태 변경(+송장등록) : PUT /api/deliveries/{id}/status?status=...&trackingNo=...
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String trackingNo) {

        deliveryService.updateStatus(id, status, trackingNo);
        return ResponseEntity.ok("배송 상태가 변경되었습니다.");
    }
}
