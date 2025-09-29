package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.exception.RedisKeyNotFoundException;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.dto.Member2FARequestDTO;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.Member2FA;
import com.imchobo.sayren_back.domain.member.exception.Not2FAUserException;
import com.imchobo.sayren_back.domain.member.exception.OTPNotMatchException;
import com.imchobo.sayren_back.domain.member.exception.UnauthorizedException;
import com.imchobo.sayren_back.domain.member.recode.QrCode;
import com.imchobo.sayren_back.domain.member.repository.Member2FARepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Member2FAServiceImpl implements Member2FAService {
  private final Member2FARepository member2faRepository;
  private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
  private final RedisUtil  redisUtil;


  // 등록용 qr코드 생성
  @Override
  public QrCode getQrCode() {
    GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
    String secret = key.getKey();

    MemberAuthDTO memberAuthDTO = SecurityUtil.getMemberAuthDTO();

    redisUtil.setMember2fa(memberAuthDTO.getId(), secret);

    return new QrCode(GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("SAYREN", memberAuthDTO.getEmail(), key));
  }

  // otp값 검증
  @Override
  public boolean verify(String secret, String otp) {
    if (secret == null) {
      throw new RedisKeyNotFoundException();
    }
    return googleAuthenticator.authorize(secret, Integer.parseInt(otp));
  }


  // 등록처리
  @Override
  public void register(Member2FARequestDTO member2FARequestDTO) {
    Member member = SecurityUtil.getMemberEntity();
    String secret = redisUtil.getMember2fa(member.getId());

    if(!verify(secret, member2FARequestDTO.getOtp())){
      throw new OTPNotMatchException();
    }

    member2faRepository.save(Member2FA.builder()
        .member(member)
        .secret(secret)
      .build());

    redisUtil.deleteMember2fa(member.getId());
  }

  @Override
  @Transactional
  public void delete(Long memberId) {
    member2faRepository.deleteByMember_Id(memberId);
  }

  @Override
  @Transactional
  public void delete() {
    delete(SecurityUtil.getMemberEntity().getId());
  }

  @Override
  public void checkOtp(Member2FARequestDTO member2FARequestDTO) {
    Member2FA member2FA = member2faRepository.findByMember(SecurityUtil.getMemberEntity()).orElseThrow(IllegalArgumentException::new);
    if(!verify(member2FA.getSecret(), member2FARequestDTO.getOtp())){
      throw new OTPNotMatchException();
    }
  }


  @Override
  public void read() {
    member2faRepository.findByMember(SecurityUtil.getMemberEntity()).orElseThrow(Not2FAUserException::new);
  }
}
