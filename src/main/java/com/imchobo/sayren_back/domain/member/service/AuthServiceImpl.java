package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.util.CookieUtil;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberLoginResponseDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.exception.EmailNotFoundException;
import com.imchobo.sayren_back.domain.member.exception.InvalidPasswordException;
import com.imchobo.sayren_back.domain.member.exception.TelNotFoundException;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final MemberMapper memberMapper;

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


    return new MemberLoginResponseDTO(accessToken, "로그인 성공");
  }

  private boolean isEmail(String username) {
    return username.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
  }
}
