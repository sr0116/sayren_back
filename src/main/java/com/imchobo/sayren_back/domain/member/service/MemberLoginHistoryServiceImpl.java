package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.common.util.UserAgentUtil;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailLoginHistoryDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberLoginHistory;
import com.imchobo.sayren_back.domain.member.mapper.MemberLoginHistoryMapper;
import com.imchobo.sayren_back.domain.member.recode.UserAgent;
import com.imchobo.sayren_back.domain.member.repository.MemberLoginHistoryRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberLoginHistoryServiceImpl implements MemberLoginHistoryService {
  private final MemberLoginHistoryRepository memberLoginHistoryRepository;
  private final UserAgentUtil userAgentUtil;
  private final MemberLoginHistoryMapper memberLoginHistoryMapper;


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

  @Override
  public PageResponseDTO<MemberDetailLoginHistoryDTO, MemberLoginHistory> getLoginHistory(Long memberId, PageRequestDTO pageRequestDTO) {
    Page<MemberLoginHistory> result = memberLoginHistoryRepository.findByMemberId(memberId, pageRequestDTO.getPageable());
    return PageResponseDTO.of(result, memberLoginHistoryMapper::toMemberDetailLoginHistoryDTO);
  }
}
