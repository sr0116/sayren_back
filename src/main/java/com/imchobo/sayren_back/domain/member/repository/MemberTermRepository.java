package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.MemberTerm;
import com.imchobo.sayren_back.domain.term.en.TermStatus;
import com.imchobo.sayren_back.domain.term.en.TermType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
  List<MemberTerm> findByTerm_TypeAndTerm_Status(TermType termType, TermStatus termStatus);
}
