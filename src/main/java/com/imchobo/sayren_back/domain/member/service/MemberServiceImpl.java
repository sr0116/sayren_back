package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.util.MailUtil;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Role;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.exception.EmailAlreadyExistsException;
import com.imchobo.sayren_back.domain.member.exception.SocialEmailAlreadyLinkedException;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

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
  private final MailUtil mailUtil;

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


    memberRepository.save(entity);
    mailUtil.emailVerification(entity.getEmail());
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

    if (email == null) {
      return false;
    }

    Member member = findByEmail(email);
    member.setEmailVerified(true);
    redisUtil.deleteEmailToken(token);
    return true;
  }
}
