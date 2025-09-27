package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {
  Optional<MemberProvider> findByProviderAndProviderUid(Provider provider, String providerUid);

  boolean existsByEmail(String email);

  Optional<MemberProvider> findByMemberAndProvider(Member member, Provider provider);

  List<MemberProvider> findByMember(Member member);
}
