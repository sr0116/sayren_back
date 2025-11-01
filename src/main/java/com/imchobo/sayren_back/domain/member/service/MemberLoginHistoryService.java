package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberDetailLoginHistoryDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.MemberListResponseDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberLoginHistory;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberLoginHistoryService {
  void saveLoginHistory(Long memberId, HttpServletRequest request);
  PageResponseDTO<MemberDetailLoginHistoryDTO, MemberLoginHistory> getLoginHistory(Long memberId, PageRequestDTO pageRequestDTO);
}
