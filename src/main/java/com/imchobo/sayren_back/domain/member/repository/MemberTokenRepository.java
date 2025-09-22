package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {
  Optional<MemberToken> findBymember(Member member);

  void deleteByMember_Id(Long memberId);

  void deleteByMemberId(Long memberId);
}
