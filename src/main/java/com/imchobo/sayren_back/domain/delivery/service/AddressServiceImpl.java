package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import com.imchobo.sayren_back.domain.delivery.mapper.AddressMapper;
import com.imchobo.sayren_back.domain.delivery.repository.AddressRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

  private final AddressRepository addressRepository;
  private final MemberRepository memberRepository;
  private final AddressMapper addressMapper;

  @Override
  public AddressResponseDTO createAddress(AddressRequestDTO dto) {
    Member member = memberRepository.findById(dto.getMemberId())
      .orElseThrow(() -> new EntityNotFoundException("회원 없음: id=" + dto.getMemberId()));

    Address address = addressMapper.toEntity(dto);
    address.setMember(member);

    // 기본 배송지 설정
    if (Boolean.TRUE.equals(dto.getIsDefault())) {
      addressRepository.findByMemberIdAndIsDefaultTrue(dto.getMemberId())
        .ifPresent(addr -> addr.setIsDefault(false));
      address.setIsDefault(true);
    }

    Address saved = addressRepository.save(address);
    return addressMapper.toResponseDTO(saved);
  }

  @Override
  public AddressResponseDTO updateAddress(Long id, AddressRequestDTO dto) {
    Address address = addressRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + id));

    address.setName(dto.getName());
    address.setTel(dto.getTel());
    address.setZipcode(dto.getZipcode());
    address.setMemo(dto.getMemo());
    address.setAddress(dto.getAddress());

    if (Boolean.TRUE.equals(dto.getIsDefault())) {
      addressRepository.findByMemberIdAndIsDefaultTrue(address.getMember().getId())
        .ifPresent(addr -> addr.setIsDefault(false));
      address.setIsDefault(true);
    }

    return addressMapper.toResponseDTO(address);
  }

  @Override
  public void deleteAddress(Long id) {
    Address address = addressRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + id));
    addressRepository.delete(address);
  }

  @Override
  public List<AddressResponseDTO> getAddressesByMember(Long memberId) {
    return addressMapper.toResponseDTOs(addressRepository.findByMemberId(memberId));
  }

  @Override
  public AddressResponseDTO getDefaultAddress(Long memberId) {
    return addressRepository.findByMemberIdAndIsDefaultTrue(memberId)
      .map(addressMapper::toResponseDTO)
      .orElseThrow(() -> new EntityNotFoundException("기본 주소 없음: memberId=" + memberId));
  }
}
