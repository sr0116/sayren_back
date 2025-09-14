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
    // 1. 약관 동의 검증
    if (!socialSignupRequestDTO.isServiceAgree() || !socialSignupRequestDTO.isPrivacyAgree()) {
      throw new IllegalArgumentException("약관에 모두 동의해야 합니다.");
    }

    try {
      // 2. 구글 id_token 검증
      GoogleIdToken idToken = googleIdTokenVerifier.verify(socialSignupRequestDTO.getIdToken());
      if (idToken == null) {
        throw new IllegalArgumentException("유효하지 않은 ID Token입니다.");
      }

      GoogleIdToken.Payload payload = idToken.getPayload();

      String email = payload.getEmail();
      if (memberRepository.existsByEmail(email)) {
        throw new SocialLinkException(); // 이미 가입된 경우
      }

      String name = (String) payload.get("name");
      String providerUid = payload.getSubject(); // sub
      boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());

      // 3. 멤버 생성
      Member member = Member.builder()
        .email(email)
        .name(name)
        .roles(Set.of(Role.USER))
        .status(MemberStatus.READY)
        .emailVerified(emailVerified)
        .build();
      memberRepository.save(member);

      // 4. 소셜 계정 연동 정보 저장
      memberProviderRepository.save(MemberProvider.builder()
        .member(member)
        .email(email)
        .provider(Provider.GOOGLE)
        .providerUid(providerUid)
        .build());

      MemberAuthDTO memberAuthDTO = memberMapper.toAuthDTO(member);

      // 5. JWT 발급
      String accessToken = jwtUtil.generateAccessToken(memberAuthDTO);
      String refreshToken = jwtUtil.generateRefreshToken(memberAuthDTO);

      // 6. Refresh Token → HttpOnly 쿠키 저장
      // 리프레쉬 토큰 쿠키에 저장
      cookieUtil.addRefreshTokenCookie(response, refreshToken, true);
      cookieUtil.addLoginCookie(response, true);

      return new MemberLoginResponseDTO(accessToken, "로그인 성공");

    } catch (Exception e) {
      throw new IllegalArgumentException("ID Token 검증 중 오류 발생", e);
    }
  }
}
