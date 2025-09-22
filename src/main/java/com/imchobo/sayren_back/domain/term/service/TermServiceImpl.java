package com.imchobo.sayren_back.domain.term.service;

import com.imchobo.sayren_back.domain.term.en.TermStatus;
import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.entity.Term;
import com.imchobo.sayren_back.domain.term.exception.TermNotFoundException;
import com.imchobo.sayren_back.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements  TermService{
  private final TermRepository termRepository;

  @Override
  public Term getTerm(TermType termType) {
    List<Term> terms = termRepository.findByTypeAndStatus(termType, TermStatus.ACTIVE);
    return terms.stream()
      .max(Comparator.comparing(term -> new ComparableVersion(term.getVersion())))
      .orElseThrow(TermNotFoundException::new);
  }
}
