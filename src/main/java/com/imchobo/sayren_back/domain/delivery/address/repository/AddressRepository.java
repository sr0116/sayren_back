package com.imchobo.sayren_back.domain.delivery.address.repository;

import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // 회원별 주소 목록
    List<Address> findByMemberIdOrderByIdDesc(Long memberId);

    // 기본 배송지 조회
    Optional<Address> findByMemberIdAndIsDefaultTrue(Long memberId);

    // 특정 회원이 기본 배송지를 이미 가지고 있는지 확인
    boolean existsByMemberIdAndIsDefaultTrue(Long memberId);
}
