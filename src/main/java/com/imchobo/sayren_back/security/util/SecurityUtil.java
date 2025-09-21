package com.imchobo.sayren_back.security.util;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
  private SecurityUtil() {}

  public static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public static boolean isUser() {
    Authentication authentication = getAuthentication();
    return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
  }

  public static boolean isGuest() {
    Authentication authentication = getAuthentication();
    return authentication == null
            || authentication instanceof AnonymousAuthenticationToken
            || !authentication.isAuthenticated();
  }

  public static MemberAuthDTO getMemberAuthDTO() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof MemberAuthDTO)) {
      throw new IllegalStateException("인증된 사용자 정보를 가져올 수 없습니다.");
    }

    return (MemberAuthDTO) authentication.getPrincipal();
  }

  public static Member getMemberEntity() {
    return Member.builder().id(getMemberAuthDTO().getId()).build();
  }
}
