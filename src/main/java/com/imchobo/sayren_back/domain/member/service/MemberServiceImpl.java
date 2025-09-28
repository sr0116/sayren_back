package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.annotation.ActiveMemberOnly;
import com.imchobo.sayren_back.domain.common.exception.RedisKeyNotFoundException;
import com.imchobo.sayren_back.domain.common.service.MailService;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.common.util.SolapiUtil;
import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.exception.*;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class MemberServiceImpl implements MemberService {
  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  private final MemberProviderRepository memberProviderRepository;
  private final RedisUtil redisUtil;
  private final MailService mailService;
  private final SolapiUtil solapiUtil;
  private final MemberTermService memberTermService;


  @Override
  @Transactional
  public void register(MemberSignupDTO memberSignupDTO) {
    Member entity = memberMapper.toEntity(memberSignupDTO);
    if (memberRepository.existsByEmail(entity.getEmail())) {
      throw new EmailAlreadyExistsException();
    }

    if (memberProviderRepository.existsByEmail(entity.getEmail())) {
      throw new SocialEmailAlreadyLinkedException();
    }


    entity.setPassword(passwordEncoder.encode(entity.getPassword()));
    entity.setStatus(MemberStatus.READY);
    entity.setEmailVerified(true);
    redisUtil.deleteEmailToken(memberSignupDTO.getToken());

    Member member = memberRepository.save(entity);

    memberTermService.saveTerm(member);
  }

  @Override
  public Member findByEmail(String email) {
    return memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
  }


  // 이메일 인증 체크하기
  @Transactional
  @Override
  public void emailVerify(String token) {
    String email = redisUtil.getEmailByToken(token);
    log.info(email);
    log.info(token);

    if (email == null) {
      return;
    }

    Member member = findByEmail(email);
    member.setEmailVerified(true);
    redisUtil.deleteEmailToken(token);
  }

  @Override
  public Member findById(Long id) {
    return memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public void sendTel(String newTel) {
    solapiUtil.sendSms(newTel);
  }



  @Override
  public void telVerify(MemberTelDTO memberTelDTO) {
    String saveTel = redisUtil.getPhoneAuth(memberTelDTO.getPhoneAuthCode());
    if (saveTel == null || saveTel.isBlank() || !saveTel.equals(memberTelDTO.getTel())) {
      throw new TelNotMatchException();
    }
  }


  @Override
  @Transactional
  public void modifyTel(MemberTelDTO memberTelDTO) {
    telVerify(memberTelDTO);
    Member member = memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new);
    member.setTel(memberTelDTO.getTel());
    member.setStatus(MemberStatus.ACTIVE);
  }

  @Override
  public FindEmailResponseDTO findEmail(MemberTelDTO memberTelDTO) {
    telVerify(memberTelDTO);
    Member member = memberRepository.findByTel(memberTelDTO.getTel()).orElse(null);
    return memberMapper.toFindEmailResponseDTO(member);
  }

  @Override
  @ActiveMemberOnly
  public Map<?, ?> getTel() {
    String tel = memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new).getTel();
    return Map.of("telinfo", tel);
  }


  @Override
  public void findPassword(FindPasswordRequestDTO findPasswordRequestDTO) {
    Member member = memberRepository.findByEmail(findPasswordRequestDTO.getEmail()).orElseThrow(EmailNotFoundException::new);
    mailService.passwordResetEmail(findPasswordRequestDTO.getEmail(), member.getId());
  }

  @Override
  @Transactional
  public void changePassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
    Long memberId;
    if(SecurityUtil.isUser()){
      memberId = SecurityUtil.getMemberAuthDTO().getId();
    }
    else {
      memberId = redisUtil.getResetPassword(resetPasswordRequestDTO.getToken());
    }
    Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
    if(passwordEncoder.matches(resetPasswordRequestDTO.getNewPassword(), member.getPassword())) {
      throw new PasswordAlreadyUseException();
    }
    member.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
    if(resetPasswordRequestDTO.getToken() != null) {
      redisUtil.deleteResetPassword(resetPasswordRequestDTO.getToken());
    }
  }


  @Override
  public void checkMail(EmailVerifyRequestDTO emailVerifyRequestDTO) {
    Member member = memberRepository.findByEmail(emailVerifyRequestDTO.getEmail()).orElse(null);
    if(member != null) {
      throw new EmailAlreadyExistsException();
    }
    mailService.emailVerification(emailVerifyRequestDTO);
  }


  @Override
  public String signupNext(String token) {
    String email = redisUtil.getEmailByToken(token);
    if(email == null) {
      throw new RedisKeyNotFoundException();
    }
    return email;
  }


  @Override
  @Transactional
  public MemberLoginResponseDTO changeName(ChangeNameDTO changeNameDTO) {
    Member member = memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new);
    if(member.getName().equals(changeNameDTO.getName())) {
      throw new RuntimeException();
    }
    member.setName(changeNameDTO.getName());
    return memberMapper.toLoginResponseDTO(memberMapper.toAuthDTO(member));
  }


  @Override
  public void passwordCheck(PasswordCheckDTO passwordCheckDTO) {
    Member member = memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new);
    if (!passwordEncoder.matches(passwordCheckDTO.getPassword(), member.getPassword())) {
      throw new InvalidPasswordException();
    }
  }
}
