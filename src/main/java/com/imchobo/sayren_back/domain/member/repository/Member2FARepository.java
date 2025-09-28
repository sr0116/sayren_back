package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.Member2FA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Member2FARepository extends JpaRepository<Member2FA, Long> {
  void deleteByMember_Id(Long memberId);
}
