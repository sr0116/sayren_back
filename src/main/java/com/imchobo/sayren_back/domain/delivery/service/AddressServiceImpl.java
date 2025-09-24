package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import com.imchobo.sayren_back.domain.delivery.mapper.AddressMapper;
import com.imchobo.sayren_back.domain.delivery.repository.AddressRepository;
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
  private final AddressMapper addressMapper;

  /**
   * 주소 등록
   */
  @Override
  public AddressResponseDTO createAddress(AddressRequestDTO dto) {
    Address address = addressMapper.toEntity(dto);

    // 기본 배송지 설정
    if (Boolean.TRUE.equals(dto.getIsDefault())) {
      addressRepository.findByMemberIdAndIsDefaultTrue(dto.getMemberId())
        .ifPresent(addr -> {
          addr.setIsDefault(false); // 기존 기본 배송지 해제
          addressRepository.save(addr);
        });
      address.setIsDefault(true);
    }

    Address saved = addressRepository.save(address);
    return addressMapper.toResponseDTO(saved);
  }

  /**
   * 주소 수정
   */
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
        .ifPresent(addr -> {
          addr.setIsDefault(false);
          addressRepository.save(addr);
        });
      address.setIsDefault(true);
    }

    return addressMapper.toResponseDTO(address);
  }

  /**
   * 주소 삭제
   */
  @Override
  public void deleteAddress(Long id) {
    Address address = addressRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("주소 없음: id=" + id));
    addressRepository.delete(address);
  }

  /**
   * 회원 주소 전체 조회
   */
  @Override
  public List<AddressResponseDTO> getAddressesByMember(Long memberId) {
    return addressMapper.toResponseDTOs(addressRepository.findByMemberId(memberId));
  }

  /**
   * 기본 주소 조회
   */
  @Override
  public AddressResponseDTO getDefaultAddress(Long memberId) {
    return addressRepository.findByMemberIdAndIsDefaultTrue(memberId)
      .map(addressMapper::toResponseDTO)
      .orElseThrow(() -> new EntityNotFoundException("기본 주소 없음: memberId=" + memberId));
  }
}
