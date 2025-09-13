package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
}
