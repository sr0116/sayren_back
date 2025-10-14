package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.DeletedMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeletedMemberRepository extends JpaRepository<DeletedMember, Long> {
  List<DeletedMember> findByEmail(String email);
}
