package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.recode.MemberDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByEmail(String email);

  Optional<Member> findByTel(String tel);

  boolean existsByEmail(String email);

  Page<Member> findAllByStatusNot(MemberStatus status, Pageable pageable);

  Page<Member> findAllByStatus(MemberStatus status, Pageable pageable);


  @Query("""
        select distinct m, t, p, mt from Member m
        left join MemberTerm t on m = t.member
        left join MemberProvider p on m = p.member
        left join Member2FA mt on m = mt.member
        where m.id = :id
    """)
  List<MemberDetail> findMemberDetail(@Param("id") Long memberId);
}
