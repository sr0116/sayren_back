package com.imchobo.sayren_back.domain.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.imchobo.sayren_back.domain.common.util.CookieUtil;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialSignupRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.TokenResponseDTO;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.en.Role;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import com.imchobo.sayren_back.domain.member.exception.EmailNotFoundException;
import com.imchobo.sayren_back.domain.member.exception.InvalidPasswordException;
import com.imchobo.sayren_back.domain.member.exception.SocialLinkException;
import com.imchobo.sayren_back.domain.member.exception.TelNotFoundException;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final MemberMapper memberMapper;
  private final GoogleIdTokenVerifier googleIdTokenVerifier;
  private final MemberProviderRepository memberProviderRepository;

  @Override
  public MemberLoginResponseDTO login(MemberLoginRequestDTO memberLoginRequestDTO, HttpServletResponse response) {
    Member member;

    // 유저네임이 이메일일 때
    if(isEmail(memberLoginRequestDTO.getUsername())){
      member = memberRepository.findByEmail(memberLoginRequestDTO.getUsername())
        .orElseThrow(EmailNotFoundException::new);
    }
    // 유저네임이 휴대폰일 때
    else {
      member = memberRepository.findByTel(memberLoginRequestDTO.getUsername())
        .orElseThrow(TelNotFoundException::new);
    }

    // DB 패스워드와 검증
    if(!passwordEncoder.matches(memberLoginRequestDTO.getPassword(), member.getPassword())){
      throw new InvalidPasswordException();
    }

    // 멤버 매핑
    MemberAuthDTO memberAuthDTO = memberMapper.toAuthDTO(member);

    // jwt 토큰 생성
    String accessToken = jwtUtil.generateAccessToken(memberAuthDTO);
    String refreshToken = jwtUtil.generateRefreshToken(memberAuthDTO);

    // 리프레쉬 토큰 쿠키에 저장
    cookieUtil.addRefreshTokenCookie(response, refreshToken, memberLoginRequestDTO.isRememberMe());
    cookieUtil.addLoginCookie(response, memberLoginRequestDTO.isRememberMe());


    return new MemberLoginResponseDTO(accessToken, "로그인 성공");
  }

  private boolean isEmail(String username) {
    return username.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
  }

  @Override
  public void logout(HttpServletResponse response) {
    cookieUtil.deleteRefreshTokenCookie(response);
    cookieUtil.deleteLoginCookie(response);
  }

  @Override
  public TokenResponseDTO accessToken(String refreshToken) {
    // 쿠키에 토큰 없으면 401
    if(refreshToken == null){
      return null;
    }

    if(!jwtUtil.isValidToken(refreshToken)){
      return null;
    }

    Member member = memberRepository.findById(Long.valueOf(jwtUtil.getClaims(refreshToken).getSubject()))
      .orElseThrow(() -> new UsernameNotFoundException("없는 유저입니다."));

    return new TokenResponseDTO(jwtUtil.generateAccessToken(memberMapper.toAuthDTO(member)));
  }

  @Override
  public MemberLoginResponseDTO socialSignup(SocialSignupRequestDTO socialSignupRequestDTO, HttpServletResponse response) {
    return null;
  }
}
