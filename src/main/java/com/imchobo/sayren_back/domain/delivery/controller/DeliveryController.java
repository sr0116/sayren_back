package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 단일 배송 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> getDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDelivery(id));
    }

    // 회원별 배송 조회
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> getDeliveriesByMember(@RequestParam Long memberId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByMember(memberId));
    }
}
