package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.exception.EmailAlreadyExistsException;
import com.imchobo.sayren_back.domain.member.exception.SocialEmailAlreadyLinkedException;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  private final MemberProviderRepository memberProviderRepository;
  @Override
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

    memberRepository.save(entity);
  }

  @Override
  public Member findByEmail(String email) {
    return memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
  }
}
