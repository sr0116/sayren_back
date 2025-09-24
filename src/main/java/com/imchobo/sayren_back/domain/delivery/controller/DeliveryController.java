package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 생성
    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> create(@RequestBody DeliveryRequestDTO dto) {
        return ResponseEntity.ok(deliveryService.create(dto));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.get(id));
    }

    // 회원별 조회
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> list(@RequestParam Long memberId) {
        return ResponseEntity.ok(deliveryService.getByMember(memberId));
    }

    // 상태 전환
    @PutMapping("/{id}/prepare")
    public ResponseEntity<DeliveryResponseDTO> prepare(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.prepare(id));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<DeliveryResponseDTO> ship(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.ship(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<DeliveryResponseDTO> complete(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.complete(id));
    }

    @PutMapping("/{id}/pickup-ready")
    public ResponseEntity<DeliveryResponseDTO> pickupReady(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.pickupReady(id));
    }

    @PutMapping("/{id}/picked-up")
    public ResponseEntity<DeliveryResponseDTO> pickedUp(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.pickedUp(id));
    }






}
