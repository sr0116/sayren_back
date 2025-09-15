package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.delivery.dto.AddressDTO; // DTO
import com.imchobo.sayren_back.domain.delivery.service.AddressService; // 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")     // 배송지 API
@RequiredArgsConstructor
public class AddressController {

  private final AddressService addressService;

//  // 등록 (addr-01) : POST /api/addresses
//  @PostMapping
//  public ResponseEntity<Long> create(@RequestBody AddressDTO dto) {
//    Long id = addressService.create(dto);
//    return ResponseEntity.ok(id);
//  }
//
//  // 기본 설정 (addr-02) : PUT /api/addresses/{addrId}/dewfault?memberId=...
//  @PutMapping("/{addrId}/default")
//  public ResponseEntity<String> setDefault(@PathVariable Long addrId, @RequestParam Long memberId) {
//    addressService.setDefault(memberId, addrId);
//    return ResponseEntity.ok("기본 배송지로 설정되었습니다.");
//  }
//
//  // 수정 : PUT /api/addresses/{addrId}
//  @PutMapping("/{addrId}")
//  public ResponseEntity<String> update(@PathVariable Long addrId, @RequestBody AddressDTO dto) {
//    dto.setAddrId(addrId);
//    addressService.update(dto);
//    return ResponseEntity.ok("배송지가 수정되었습니다.");
//  }
//
//  // 삭제 (addr-03) : DELETE /api/addresses/{addrId}?memberId=...
//  @DeleteMapping("/{addrId}")
//  public ResponseEntity<String> delete(@PathVariable Long addrId, @RequestParam Long memberId) {
//    addressService.delete(memberId, addrId);
//    return ResponseEntity.ok("배송지가 삭제되었습니다.");
//  }
//
//  // 목록 : GET /api/addresses?memberId=...
//  @GetMapping
//  public ResponseEntity<List<AddressDTO>> list(@RequestParam Long memberId) {
//    return ResponseEntity.ok(addressService.list(memberId));
//  }
//
//  // 단건 : GET /api/addresses/{addrId}?memberId=...
//  @GetMapping("/{addrId}")
//  public ResponseEntity<AddressDTO> get(@PathVariable Long addrId, @RequestParam Long memberId) {
//    return ResponseEntity.ok(addressService.get(memberId, addrId));
//  }
}
