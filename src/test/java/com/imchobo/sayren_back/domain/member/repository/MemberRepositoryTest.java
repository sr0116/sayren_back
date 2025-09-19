package com.imchobo.sayren_back.domain.member.repository;

import com.imchobo.sayren_back.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;
  @Autowired
  PasswordEncoder passwordEncoder;

  @Test
  void updatePassword(){
    Member member = memberRepository.findByEmail("manlubo11@gmail.com").get();
    member.setPassword(passwordEncoder.encode("12345678"));
    memberRepository.save(member);
  }

}