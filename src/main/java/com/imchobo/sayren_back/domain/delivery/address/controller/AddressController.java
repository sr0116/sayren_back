package com.imchobo.sayren_back.domain.delivery.address.controller;

import com.imchobo.sayren_back.domain.delivery.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    //배송지 등록
    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody @Valid AddressRequestDTO dto) {
        log.info("[배송지 등록 요청]");
        AddressResponseDTO result = addressService.createAddress(dto);
        return ResponseEntity.ok(Map.of("message", "success", "data", result));
    }

    //배송지 수정
    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(
      @PathVariable Long addressId,
      @RequestBody @Valid AddressRequestDTO dto
    ) {
        log.info("[배송지 수정 요청] addressId={}", addressId);
        AddressResponseDTO result = addressService.updateAddress(addressId, dto);
        return ResponseEntity.ok(Map.of("message", "success", "data", result));
    }

    // 배송지 삭제
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        log.info("[배송지 삭제 요청] addressId={}", addressId);
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(Map.of("message", "success"));
    }

    //회원 배송지 목록 조회
    @GetMapping
    public ResponseEntity<?> getAddressesByMember() {
        log.info("[배송지 목록 조회 요청]");
        List<AddressResponseDTO> list = addressService.getAddressesByMember();
        return ResponseEntity.ok(Map.of("message", "success", "data", list));
    }

    // 기본 배송지 조회
    @GetMapping("/default")
    public ResponseEntity<?> getDefaultAddress() {
        log.info("[기본 배송지 조회 요청]");
        AddressResponseDTO result = addressService.getDefaultAddress();
        return ResponseEntity.ok(Map.of("message", "success", "data", result));
    }
}
