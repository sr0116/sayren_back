package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateRequest;
import com.imchobo.sayren_back.domain.delivery.dto.AddressCreateResponse;
import com.imchobo.sayren_back.domain.delivery.dto.AddressDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import com.imchobo.sayren_back.domain.delivery.mapper.AddressMapper;
import com.imchobo.sayren_back.domain.delivery.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
  public AddressCreateResponse create(AddressCreateRequest request) {
    Address entity = addressMapper.toEntity(request);

    // 기본 배송지로 등록하려는 경우 → 기존 기본 배송지 해제
    if (Boolean.TRUE.equals(entity.getDefaultAddress())) {
      addressRepository.findByMemberIdAndDefaultAddressTrue(entity.getMemberId())
        .ifPresent(prev -> prev.setDefaultAddress(false));
    }

    Address saved = addressRepository.save(entity);

    // 반드시 PK 값이 반환되도록 확인
    if (saved.getAddrId() == null) {
      throw new IllegalStateException("주소 등록 후 addrId가 생성되지 않았습니다.");
    }

    return addressMapper.toCreateResponse(saved);
  }

  /**
   * 주소 단건 조회
   */
  @Override
  @Transactional(readOnly = true)
  public AddressDTO getById(Long addrId) {
    if (addrId == null) {
      throw new IllegalArgumentException("id 값이 null 입니다!");
    }

    Address entity = addressRepository.findById(addrId)
      .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다. id=" + addrId));

    return addressMapper.toDTO(entity);
  }

  /**
   * 특정 회원의 모든 주소 조회
   */
  @Override
  @Transactional(readOnly = true)
  public List<AddressDTO> getByMemberId(Long memberId) {
    return addressRepository.findByMemberId(memberId).stream()
      .map(addressMapper::toDTO)
      .collect(Collectors.toList());
  }

  /**
   * 기본 배송지 설정
   */
  @Override
  public void setDefault(Long memberId, Long addrId) {
    addressRepository.findByMemberIdAndDefaultAddressTrue(memberId)
      .ifPresent(prev -> prev.setDefaultAddress(false));

    Address target = addressRepository.findById(addrId)
      .orElseThrow(() -> new EntityNotFoundException("주소를 찾을 수 없습니다. id=" + addrId));

    if (!target.getMemberId().equals(memberId)) {
      throw new IllegalArgumentException("본인 주소만 기본 설정할 수 있습니다.");
    }

    target.setDefaultAddress(true);
  }

  /**
   * 주소 수정
   */
  @Override
  public void update(AddressDTO dto) {
    Address entity = addressRepository.findById(dto.getAddrId())
      .orElseThrow(() -> new EntityNotFoundException("주소를 찾을 수 없습니다. id=" + dto.getAddrId()));

    entity.setName(dto.getName());
    entity.setTel(dto.getTel());
    entity.setZipcode(dto.getZipcode());
    entity.setAddress(dto.getAddress());
    entity.setMemo(dto.getMemo());

    if (Boolean.TRUE.equals(dto.getDefaultAddress())) {
      addressRepository.findByMemberIdAndDefaultAddressTrue(dto.getMemberId())
        .ifPresent(prev -> prev.setDefaultAddress(false));
      entity.setDefaultAddress(true);
    }
  }

  /**
   * 주소 삭제
   */
  @Override
  public void delete(Long memberId, Long addrId) {
    Address target = addressRepository.findById(addrId)
      .orElseThrow(() -> new EntityNotFoundException("주소를 찾을 수 없습니다. id=" + addrId));

    if (!target.getMemberId().equals(memberId)) {
      throw new IllegalArgumentException("본인 주소만 삭제할 수 있습니다.");
    }

    boolean wasDefault = Boolean.TRUE.equals(target.getDefaultAddress());
    addressRepository.delete(target);

    if (wasDefault) {
      addressRepository.findByMemberIdOrderByAddrIdDesc(memberId).stream()
        .findFirst()
        .ifPresent(prev -> prev.setDefaultAddress(true));
    }
  }
}
