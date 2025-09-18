package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.util.CookieUtil;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import com.imchobo.sayren_back.domain.member.exception.EmailNotFoundException;
import com.imchobo.sayren_back.domain.member.exception.InvalidPasswordException;
import com.imchobo.sayren_back.domain.member.exception.TelNotFoundException;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final MemberMapper memberMapper;
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

    return tokensAndLoginResponse(member, response, memberLoginRequestDTO.isRememberMe());
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
  @Transactional
  public MemberLoginResponseDTO socialSignup(SocialSignupRequestDTO socialSignupRequestDTO, HttpServletResponse response) {
    SocialUser socialUser = socialSignupRequestDTO.getSocialUser();


    Member member = Member.builder().name(socialUser.name()).email(socialUser.email()).status(MemberStatus.READY).emailVerified(true).build();

    memberRepository.save(member);
    memberProviderRepository.save(MemberProvider.builder().providerUid(socialUser.providerUid()).member(member).provider(socialUser.provider()).email(socialUser.email()).build());


    return tokensAndLoginResponse(member, response, true);
  }

  @Override
  @Transactional
  public MemberLoginResponseDTO socialLink(SocialLinkRequestDTO socialLinkRequestDTO, HttpServletResponse response) {
    SocialUser socialUser = socialLinkRequestDTO.getSocialUser();

    Member member = memberRepository.findByEmail(socialUser.email())
            .orElseThrow(EmailNotFoundException::new);

    if(!passwordEncoder.matches(socialLinkRequestDTO.getPassword(), member.getPassword())){
      throw new InvalidPasswordException();
    }

    memberProviderRepository.save(MemberProvider.builder().providerUid(socialUser.providerUid()).member(member).provider(socialUser.provider()).email(socialUser.email()).build());
    member.setEmailVerified(true);


    return tokensAndLoginResponse(member, response, true);
  }

  private MemberLoginResponseDTO tokensAndLoginResponse(Member member,
                                                        HttpServletResponse response,
                                                        boolean rememberMe) {
    // 멤버 매핑
    MemberAuthDTO memberAuthDTO = memberMapper.toAuthDTO(member);

    // jwt 토큰 생성
    String accessToken = jwtUtil.generateAccessToken(memberAuthDTO);
    String refreshToken = jwtUtil.generateRefreshToken(memberAuthDTO);

    // 리프레쉬 토큰 쿠키에 저장
    cookieUtil.addRefreshTokenCookie(response, refreshToken, rememberMe);
    cookieUtil.addLoginCookie(response, rememberMe);

    return new MemberLoginResponseDTO(accessToken, "로그인 성공");
  }

}
