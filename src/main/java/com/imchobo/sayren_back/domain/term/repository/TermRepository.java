package com.imchobo.sayren_back.domain.term.repository;

import com.imchobo.sayren_back.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}
