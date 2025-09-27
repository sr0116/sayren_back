package com.imchobo.sayren_back.domain.term.repository;

import com.imchobo.sayren_back.domain.term.en.TermStatus;
import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {
  List<Term> findByTypeAndStatus(TermType type, TermStatus status);
}
