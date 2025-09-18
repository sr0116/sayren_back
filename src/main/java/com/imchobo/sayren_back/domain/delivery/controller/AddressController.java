package com.imchobo.sayren_back.domain.delivery.controller;

import com.imchobo.sayren_back.domain.delivery.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

  private final AddressService addressService;

  @PostMapping
  public ResponseEntity<AddressResponseDTO> create(@RequestBody AddressRequestDTO dto) {
    return ResponseEntity.ok(addressService.createAddress(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AddressResponseDTO> update(@PathVariable Long id, @RequestBody AddressRequestDTO dto) {
    return ResponseEntity.ok(addressService.updateAddress(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    addressService.deleteAddress(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/member/{memberId}")
  public ResponseEntity<List<AddressResponseDTO>> getByMember(@PathVariable Long memberId) {
    return ResponseEntity.ok(addressService.getAddressesByMember(memberId));
  }

  @GetMapping("/member/{memberId}/default")
  public ResponseEntity<AddressResponseDTO> getDefault(@PathVariable Long memberId) {
    return ResponseEntity.ok(addressService.getDefaultAddress(memberId));
  }
}
