package com.imchobo.sayren_back.domain.delivery.address.service;

import com.imchobo.sayren_back.domain.delivery.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.address.mapper.AddressMapper;
import com.imchobo.sayren_back.domain.delivery.address.repository.AddressRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO dto) {
        Member member = SecurityUtil.getMemberEntity();
        Address address = addressMapper.toEntity(dto, member);

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            addressRepository.findByMemberIdAndIsDefaultTrue(member.getId())
              .ifPresent(existing -> {
                  existing.setIsDefault(false);
                  addressRepository.save(existing);
              });
            address.setIsDefault(true);
        } else if (!addressRepository.existsByMemberIdAndIsDefaultTrue(member.getId())) {
            address.setIsDefault(true);
        }

        Address saved = addressRepository.save(address);
        log.info("배송지 등록 완료 → memberId={}, addressId={}", member.getId(), saved.getId());
        return addressMapper.toResponseDTO(saved);
    }

    @Override
    public AddressResponseDTO updateAddress(Long id, AddressRequestDTO dto) {
        Member member = SecurityUtil.getMemberEntity();
        Address address = addressRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("배송지를 찾을 수 없습니다. id=" + id));

        if (!address.getMember().getId().equals(member.getId())) {
            throw new SecurityException("본인 소유의 배송지만 수정할 수 있습니다.");
        }

        addressMapper.updateEntityFromDto(dto, address);

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            addressRepository.findByMemberIdAndIsDefaultTrue(member.getId())
              .ifPresent(existing -> {
                  existing.setIsDefault(false);
                  addressRepository.save(existing);
              });
            address.setIsDefault(true);
        }

        Address updated = addressRepository.save(address);
        log.info("배송지 수정 완료 → memberId={}, addressId={}", member.getId(), updated.getId());
        return addressMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteAddress(Long id) {
        Member member = SecurityUtil.getMemberEntity();
        Address address = addressRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("배송지를 찾을 수 없습니다. id=" + id));

        if (!address.getMember().getId().equals(member.getId())) {
            throw new SecurityException("본인 소유의 배송지만 삭제할 수 있습니다.");
        }

        addressRepository.delete(address);
        log.info("배송지 삭제 완료 → memberId={}, addressId={}", member.getId(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByMember() {
        Member member = SecurityUtil.getMemberEntity();
        List<Address> addresses = addressRepository.findByMemberIdOrderByIdDesc(member.getId());
        log.info("배송지 목록 조회 완료 → memberId={}, count={}", member.getId(), addresses.size());
        return addressMapper.toResponseDTOs(addresses);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponseDTO getDefaultAddress() {
        Member member = SecurityUtil.getMemberEntity();
        Address address = addressRepository.findByMemberIdAndIsDefaultTrue(member.getId())
          .orElseThrow(() -> new EntityNotFoundException("기본 배송지가 존재하지 않습니다."));
        log.info("기본 배송지 조회 완료 → memberId={}, addressId={}", member.getId(), address.getId());
        return addressMapper.toResponseDTO(address);
    }
}
