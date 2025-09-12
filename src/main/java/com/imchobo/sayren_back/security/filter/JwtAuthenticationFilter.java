package com.imchobo.sayren_back.security.filter;

import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.service.CustomUserDetailsService;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {

    String accessToken = jwtUtil.resolveToken(request);

    try {
      if (accessToken != null) {
        String email = null;

        try {
          // Access Token 정상 → Claims 추출
          email = jwtUtil.validateToken(accessToken).getSubject();
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
          // 만료된 토큰 → Claims는 남아있음
          email = ex.getClaims().getSubject();

          log.info("만료된 Access Token 감지, email: {}", email);

          // 여기서 Refresh Token 확인 후 Access Token 재발급 로직을 태울 수 있음
          // ex) redis/db 조회 → 유효하면 새 Access Token 만들어서 response에 넣기
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          MemberAuthDTO member =
                  (MemberAuthDTO) customUserDetailsService.loadUserByUsername(email);

          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(
                          member,
                          null,
                          member.getAuthorities()
                  );

          authentication.setDetails(
                  new WebAuthenticationDetailsSource().buildDetails(request)
          );

          // 시큐리티 컨텍스트에 저장
          SecurityContextHolder.getContext().setAuthentication(authentication);

          log.info("JWT 인증 성공 : {}", email);
        }
      }
    } catch (Exception e) {
      log.error("JWT 인증 필터 오류", e);
    }

    filterChain.doFilter(request, response);
  }
}
