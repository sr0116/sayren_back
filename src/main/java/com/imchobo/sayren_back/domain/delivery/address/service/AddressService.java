package com.imchobo.sayren_back.domain.delivery.address.service;

import com.imchobo.sayren_back.domain.delivery.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;

import java.util.List;

public interface AddressService {
    AddressResponseDTO createAddress(Member member, AddressRequestDTO dto);
    AddressResponseDTO updateAddress(Member member, Long id, AddressRequestDTO dto);
    void deleteAddress(Member member, Long id);
    List<AddressResponseDTO> getAddressesByMember(Long memberId);
    AddressResponseDTO getDefaultAddress(Long memberId);
}
