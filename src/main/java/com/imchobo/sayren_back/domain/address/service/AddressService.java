package com.imchobo.sayren_back.domain.address.service;

import com.imchobo.sayren_back.domain.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.address.dto.AddressResponseDTO;

import java.util.List;

public interface AddressService {
  AddressResponseDTO createAddress(AddressRequestDTO dto);
  AddressResponseDTO updateAddress(Long id, AddressRequestDTO dto);
  void deleteAddress(Long id);
  List<AddressResponseDTO> getAddressesByMember(Long memberId);
  AddressResponseDTO getDefaultAddress(Long memberId);
}
