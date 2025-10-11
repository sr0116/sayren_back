package com.imchobo.sayren_back.domain.delivery.address.controller;

import com.imchobo.sayren_back.domain.delivery.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.address.service.AddressService;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    //  배송지 등록
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody AddressRequestDTO dto) {
        Member member = SecurityUtil.getMemberEntity();
        return ResponseEntity.ok(addressService.createAddress(member, dto));
    }

    // 배송지 수정
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequestDTO dto
    ) {
        Member member = SecurityUtil.getMemberEntity();
        return ResponseEntity.ok(addressService.updateAddress(member, addressId, dto));
    }

    //  배송지 삭제
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        Member member = SecurityUtil.getMemberEntity();
        addressService.deleteAddress(member, addressId);
        return ResponseEntity.noContent().build();
    }

    // 로그인 회원 배송지 목록 조회
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByMember() {
        Member member = SecurityUtil.getMemberEntity();
        return ResponseEntity.ok(addressService.getAddressesByMember(member.getId()));
    }

    //  기본 배송지 조회
    @GetMapping("/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress() {
        Member member = SecurityUtil.getMemberEntity();
        return ResponseEntity.ok(addressService.getDefaultAddress(member.getId()));
    }
}
