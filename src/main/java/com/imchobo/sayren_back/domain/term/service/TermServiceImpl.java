package com.imchobo.sayren_back.domain.term.service;

import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.recode.LatestTerms;
import com.imchobo.sayren_back.domain.term.en.TermStatus;
import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.entity.Term;
import com.imchobo.sayren_back.domain.term.exception.TermNotFoundException;
import com.imchobo.sayren_back.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements  TermService{
  private final TermRepository termRepository;
  private final RedisUtil redisUtil;

  @Override
  public Term getTerm(TermType termType) {
    List<Term> terms = termRepository.findByTypeAndStatus(termType, TermStatus.ACTIVE);
    return terms.stream()
      .max(Comparator.comparing(term -> new ComparableVersion(term.getVersion())))
      .orElseThrow(TermNotFoundException::new);
  }

  @Override
  public LatestTerms getLatestTerms() {
    Term service = getTerm(TermType.SERVICE);
    Term privacy = getTerm(TermType.PRIVACY);
    return new LatestTerms(service, privacy);
  }

  @Override
  @EventListener(ApplicationReadyEvent.class)
  public void preloadTerms() {
    redisUtil.setTermLatest(getLatestTerms());
  }
}
