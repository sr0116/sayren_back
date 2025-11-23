package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.Member2FA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Member2FARepository extends JpaRepository<Member2FA, Long> {
  void deleteByMember_Id(Long memberId);

  Optional<Member2FA> findByMember(Member member);
}
