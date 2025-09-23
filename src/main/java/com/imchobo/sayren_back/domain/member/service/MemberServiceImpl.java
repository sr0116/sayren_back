package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.service.MailService;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.common.util.SolapiUtil;
import com.imchobo.sayren_back.domain.member.dto.FindEmailResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.dto.MemberTelDTO;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.exception.EmailAlreadyExistsException;
import com.imchobo.sayren_back.domain.member.exception.SocialEmailAlreadyLinkedException;
import com.imchobo.sayren_back.domain.member.exception.TelNotMatchException;
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

    log.info(entity);


    Member member = memberRepository.save(entity);
    mailService.emailVerification(entity.getEmail());

    memberTermService.saveTerm(member);
  }

  @Override
  public Member findByEmail(String email) {
    return memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
  }


  // 이메일 인증 체크하기
  @Transactional
  @Override
  public boolean emailVerify(String token) {
    String email = redisUtil.getEmailByToken(token);
    log.info(email);
    log.info(token);

    if (email == null) {
      return false;
    }

    Member member = findByEmail(email);
    member.setEmailVerified(true);
    redisUtil.deleteEmailToken(token);
    return true;
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
  public Member telVerify(MemberTelDTO memberTelDTO) {
    String saveTel = redisUtil.getPhoneAuth(memberTelDTO.getPhoneAuthCode());
    if (saveTel == null || saveTel.isBlank() || !saveTel.equals(memberTelDTO.getTel())) {
      throw new TelNotMatchException();
    }
    return memberRepository.findById(SecurityUtil.getMemberAuthDTO().getId()).orElseThrow(IllegalArgumentException::new);
  }

  @Override
  @Transactional
  public void modifyTel(MemberTelDTO memberTelDTO) {
    Member member = telVerify(memberTelDTO);
    member.setTel(memberTelDTO.getTel());
    member.setStatus(MemberStatus.ACTIVE);
  }

  @Override
  public FindEmailResponseDTO findEmail(MemberTelDTO memberTelDTO) {
    Member member = telVerify(memberTelDTO);
    return memberMapper.toFindEmailResponseDTO(member);
  }
}
