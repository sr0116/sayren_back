package com.imchobo.sayren_back.domain.member.en;

public enum TokenStatus {
  ACTIVE,     // 유효 (로그인 유지에 사용 가능)
  EXPIRED,    // 만료일이 지나서 무효
  REVOKED,    // 강제 만료 (로그아웃, 관리자 차단 등)
  BLACKLISTED // 보안 이슈로 블랙리스트 처리
}