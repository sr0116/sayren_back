package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.MemberLoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLoginHistoryRepository extends JpaRepository<MemberLoginHistory, Long> {
  Page<MemberLoginHistory> findByMemberId(Long memberId, Pageable pageable);
}
