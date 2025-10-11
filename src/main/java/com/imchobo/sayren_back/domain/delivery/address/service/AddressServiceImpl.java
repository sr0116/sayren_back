package com.imchobo.sayren_back.domain.delivery.address.service;

import com.imchobo.sayren_back.domain.delivery.address.dto.AddressRequestDTO;
import com.imchobo.sayren_back.domain.delivery.address.dto.AddressResponseDTO;
import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.address.mapper.AddressMapper;
import com.imchobo.sayren_back.domain.delivery.address.repository.AddressRepository;
import com.imchobo.sayren_back.domain.member.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    // 🏗 배송지 등록
    @Override
    public AddressResponseDTO createAddress(Member member, AddressRequestDTO dto) {
        if (member == null || member.getId() == null) {
            throw new IllegalStateException("로그인 정보가 없습니다. 다시 로그인해주세요.");
        }

        Address address = addressMapper.toEntity(dto, member);

        // 기본배송지 설정 시 기존 기본배송지 해제
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            addressRepository.findByMemberIdAndIsDefaultTrue(member.getId())
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        addressRepository.save(existing);
                    });
            address.setIsDefault(true);
        } else {
            // 기본배송지가 하나도 없을 경우 자동 기본 설정
            boolean hasDefault = addressRepository.existsByMemberIdAndIsDefaultTrue(member.getId());
            if (!hasDefault) address.setIsDefault(true);
        }

        Address saved = addressRepository.save(address);
        log.info(" 새 배송지 등록 완료 → memberId={}, addressId={}", member.getId(), saved.getId());

        return addressMapper.toResponseDTO(saved);
    }

    //  배송지 수정
    @Override
    public AddressResponseDTO updateAddress(Member member, Long id, AddressRequestDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송지를 찾을 수 없습니다. id=" + id));

        // 본인 소유 검증
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

    // 🗑 배송지 삭제
    @Override
    public void deleteAddress(Member member, Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송지를 찾을 수 없습니다. id=" + id));

        if (!address.getMember().getId().equals(member.getId())) {
            throw new SecurityException("본인 소유의 배송지만 삭제할 수 있습니다.");
        }

        addressRepository.delete(address);
        log.info(" 배송지 삭제 완료 → memberId={}, addressId={}", member.getId(), id);
    }

    //  회원별 배송지 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByMember(Long memberId) {
        List<Address> addresses = addressRepository.findByMemberIdOrderByIdDesc(memberId);
        log.info(" 배송지 목록 조회 → memberId={}, count={}", memberId, addresses.size());
        return addressMapper.toResponseDTOs(addresses);
    }

    //  기본 배송지 조회
    @Override
    @Transactional(readOnly = true)
    public AddressResponseDTO getDefaultAddress(Long memberId) {
        Address address = addressRepository.findByMemberIdAndIsDefaultTrue(memberId)
                .orElseThrow(() -> new EntityNotFoundException("기본 배송지가 존재하지 않습니다."));
        log.info("기본 배송지 조회 완료 → memberId={}, addressId={}", memberId, address.getId());
        return addressMapper.toResponseDTO(address);
    }
}
