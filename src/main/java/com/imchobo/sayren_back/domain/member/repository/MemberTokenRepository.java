package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.MemberToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {
}
