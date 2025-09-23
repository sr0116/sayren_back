package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberTerm;
import com.imchobo.sayren_back.domain.member.recode.LatestTerms;
import com.imchobo.sayren_back.domain.member.repository.MemberTermRepository;
import com.imchobo.sayren_back.domain.term.en.TermType;
import com.imchobo.sayren_back.domain.term.entity.Term;
import com.imchobo.sayren_back.domain.term.service.TermService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberTermServiceImpl implements MemberTermService {
  private final MemberTermRepository memberTermRepository;
  private final TermService termService;
  private final RedisUtil redisUtil;

  @Override
  @Transactional
  public void saveTerm(Member member) {
    LatestTerms latestTerms = redisUtil.getLatestTerms();

    memberTermRepository.save(
      MemberTerm.builder()
        .member(member)
        .agreed(true)
        .term(latestTerms.service())
        .version(latestTerms.service().getVersion())
        .build()
    );

    memberTermRepository.save(
      MemberTerm.builder()
        .member(member)
        .agreed(true)
        .term(latestTerms.privacy())
        .version(latestTerms.privacy().getVersion())
        .build()
    );
  }
}
