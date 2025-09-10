package com.imchobo.sayren_back.security.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member;
    if (isEmail(username)) {
      member = memberRepository.findByEmail(username)
              .orElseThrow(() -> new UsernameNotFoundException(username + " - 회원 없음"));
    }
    else {
      member = memberRepository.findByTel(username)
              .orElseThrow(() -> new UsernameNotFoundException(username + " - 회원 없음"));
    }

    return memberMapper.toAuthDTO(member);
  }

  public boolean isEmail(String username) {
    return username.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
  }
}
