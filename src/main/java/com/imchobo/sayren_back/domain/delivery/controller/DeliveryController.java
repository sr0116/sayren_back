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

    // ── 기본 CRUD ────────────────────────────────

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

    // ── 상태 전환 ────────────────────────────────

    // 배송 시작 (READY → SHIPPING)
    @PutMapping("/{id}/ship")
    public ResponseEntity<DeliveryResponseDTO> ship(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.ship(id));
    }

    // 배송 완료 (SHIPPING → DELIVERED)
    @PutMapping("/{id}/complete")
    public ResponseEntity<DeliveryResponseDTO> complete(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.complete(id));
    }

    // 회수 준비 (DELIVERED → RETURN_READY)
    @PutMapping("/{id}/return-ready")
    public ResponseEntity<DeliveryResponseDTO> returnReady(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.returnReady(id));
    }

    // 회수 중 (RETURN_READY → IN_RETURNING)
    @PutMapping("/{id}/in-returning")
    public ResponseEntity<DeliveryResponseDTO> inReturning(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.inReturning(id));
    }

    // 회수 완료 (IN_RETURNING → RETURNED)
    @PutMapping("/{id}/returned")
    public ResponseEntity<DeliveryResponseDTO> returned(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.returned(id));
    }
}
