package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.util.CookieUtil;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.RedisTokenDTO;
import com.imchobo.sayren_back.domain.member.en.TokenStatus;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberToken;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberTokenRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberTokenServiceImpl implements MemberTokenService {

  private final MemberTokenRepository memberTokenRepository;
  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final MemberMapper memberMapper;
  private final RedisUtil redisUtil;

  @Override
  public MemberLoginResponseDTO saveToken(Member member, HttpServletResponse response, boolean rememberMe) {
    MemberAuthDTO authDTO = memberMapper.toAuthDTO(member);

    String accessToken = jwtUtil.generateAccessToken(authDTO);
    String refreshToken = jwtUtil.generateRefreshToken(authDTO);

    // 리프레쉬 토큰 쿠키에 저장
    cookieUtil.addRefreshTokenCookie(response, refreshToken, rememberMe);
    cookieUtil.addAccsessCookie(response, accessToken);
    cookieUtil.addLoginCookie(response, rememberMe);

    memberTokenRepository.save(
      MemberToken.builder()
        .member(member)
        .token(refreshToken)
        .status(TokenStatus.ACTIVE)
        .build()
    );
    RedisTokenDTO redisTokenDTO = RedisTokenDTO.builder()
      .tokenStatus(TokenStatus.ACTIVE)
      .token(refreshToken)
      .build();
    redisUtil.setRefreshToken(redisTokenDTO);

    return memberMapper.toLoginResponseDTO(authDTO);
  }

  @Override
  public Long validateAndGetMemberId(String refreshToken) {
    if (refreshToken == null || !jwtUtil.isValidToken(refreshToken)) {
      return null;
    }

    Long memberId = Long.valueOf(jwtUtil.getClaims(refreshToken).getSubject());

    RedisTokenDTO redisTokenDTO = redisUtil.getRefreshToken(memberId);

    if (redisTokenDTO != null) {
      if(!refreshToken.equals(redisTokenDTO.getToken())) {
        return null;
      }
      return memberId;
    }

    MemberToken memberToken = getMemberToken(memberId);
    if(memberToken == null) {
      return null;
    }

    if(!refreshToken.equals(memberToken.getToken())) {
      return null;
    }

    RedisTokenDTO dto = RedisTokenDTO.builder()
      .tokenStatus(memberToken.getStatus())
      .token(memberToken.getToken())
      .build();

    redisUtil.setRefreshToken(dto);

    return memberId;
  }

  @Override
  public MemberToken getMemberToken(Long memberId) {
    return memberTokenRepository.findBymember(Member.builder().id(memberId).build())
      .orElse(null);
  }


  @Override
  public void deleteMemberToken(Long memberId) {
    redisUtil.deleteRefreshToken(memberId);
    memberTokenRepository.deleteByMember_Id(memberId);
  }

  @Override
  public void deleteMemberToken(String refreshToken) {
    try {
      Claims claims = jwtUtil.getClaims(refreshToken);
      Long memberId = Long.valueOf(claims.getSubject());
      redisUtil.deleteRefreshToken(memberId);
      memberTokenRepository.deleteByMemberId(memberId);
    } catch (ExpiredJwtException e) {
      Long memberId = Long.valueOf(e.getClaims().getSubject());
      redisUtil.deleteRefreshToken(memberId);
      memberTokenRepository.deleteByMemberId(memberId);
    }
  }
}
