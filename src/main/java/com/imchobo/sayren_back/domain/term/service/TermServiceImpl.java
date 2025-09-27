package com.imchobo.sayren_back.domain.term.service;

import com.imchobo.sayren_back.domain.common.util.NextUtil;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.recode.LatestTerms;
import com.imchobo.sayren_back.domain.term.dto.TermResponseDTO;
import com.imchobo.sayren_back.domain.term.en.TermStatus;
import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.entity.Term;
import com.imchobo.sayren_back.domain.term.exception.TermNotFoundException;
import com.imchobo.sayren_back.domain.term.mapper.TermMapper;
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
  private final TermMapper termMapper;
  private final RedisUtil redisUtil;
  private final NextUtil nextUtil;

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
  public TermResponseDTO getLatestTerm(TermType termType) {
    return termMapper.toResponseDTO(getTerm(termType));
  }


  @Override
  public void revalidateTerm(TermType termType) {
    String url = termType.toString().toLowerCase();
    nextUtil.revalidatePaths(List.of("/api/terms/" + url, "/terms/" + url));
  }
}
