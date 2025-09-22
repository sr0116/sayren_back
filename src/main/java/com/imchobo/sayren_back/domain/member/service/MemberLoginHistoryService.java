package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberLoginHistoryService {
  void saveLoginHistory(Long memberId, HttpServletRequest request);
}
