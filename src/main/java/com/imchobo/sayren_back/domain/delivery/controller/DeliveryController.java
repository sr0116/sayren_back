package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/user/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;


    //  배송 생성 (사용자 직접 생성용)

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DeliveryRequestDTO dto) {
        log.info("[배송 생성 요청]");
        DeliveryResponseDTO result = deliveryService.create(dto);
        return ResponseEntity.ok(Map.of("message", "success", "data", result));
    }


     //단일 배송 조회

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        log.info("[배송 단건 조회] id={}", id);
        DeliveryResponseDTO result = deliveryService.get(id);
        return ResponseEntity.ok(Map.of("message", "success", "data", result));
    }

    //로그인 회원의 배송 내역 조회

    @GetMapping
    public ResponseEntity<?> list() {
        Long memberId = SecurityUtil.getMemberAuthDTO().getId();
        log.info("[회원 배송 내역 조회] memberId={}", memberId);
        List<DeliveryResponseDTO> list = deliveryService.getByMember(memberId);
        return ResponseEntity.ok(Map.of("message", "success", "data", list));
    }

}
