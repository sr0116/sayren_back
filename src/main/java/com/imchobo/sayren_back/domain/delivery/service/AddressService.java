package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.AddressResponseDTO;

import java.util.List;

public interface AddressService {
  AddressResponseDTO createAddress(AddressRequestDTO dto);
  AddressResponseDTO updateAddress(Long id, AddressRequestDTO dto);
  void deleteAddress(Long id);
  List<AddressResponseDTO> getAddressesByMember(Long memberId);
  AddressResponseDTO getDefaultAddress(Long memberId);
}
