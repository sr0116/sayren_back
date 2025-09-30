package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByEmail(String email);

  Optional<Member> findByTel(String tel);

  boolean existsByEmail(String email);

  Page<Member> findAllByStatusNot(MemberStatus status, Pageable pageable);

  Page<Member> findAllByStatus(MemberStatus status, Pageable pageable);

}
