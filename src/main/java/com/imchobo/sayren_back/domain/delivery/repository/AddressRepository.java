package com.imchobo.sayren_back.domain.delivery.repository;

import com.imchobo.sayren_back.domain.delivery.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

  // 회원 주소 목록 (최근 addrId 순 정렬)
  List<Address> findByMemberIdOrderByAddrIdDesc(Long memberId);

  // 기본 배송지 조회
  Optional<Address> findByMemberIdAndDefaultAddressTrue(Long memberId);

  // 회원 ID로 모든 주소 조회
  List<Address> findByMemberId(Long memberId);
}
