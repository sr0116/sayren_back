package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {
}
