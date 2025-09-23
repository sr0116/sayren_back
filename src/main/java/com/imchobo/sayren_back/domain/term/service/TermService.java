package com.imchobo.sayren_back.domain.term.service;

import com.imchobo.sayren_back.domain.member.recode.LatestTerms;
import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.entity.Term;

public interface TermService {
  Term getTerm(TermType termType);
  LatestTerms getLatestTerms();
  void preloadTerms();
}
