package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.util.UserAgentUtil;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberLoginHistory;
import com.imchobo.sayren_back.domain.member.recode.UserAgent;
import com.imchobo.sayren_back.domain.member.repository.MemberLoginHistoryRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberLoginHistoryServiceImpl implements MemberLoginHistoryService {
  private final MemberLoginHistoryRepository memberLoginHistoryRepository;
  private final UserAgentUtil userAgentUtil;


  @Override
  @Transactional
  public void saveLoginHistory(Long memberId, HttpServletRequest request) {
    UserAgent userAgent = userAgentUtil.getUserAgent(request);

    memberLoginHistoryRepository.save(MemberLoginHistory.builder()
        .member(Member.builder().id(memberId).build())
        .ip(userAgent.ip())
        .device(userAgent.device())
      .build());
  }

}
