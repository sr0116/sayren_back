package com.imchobo.sayren_back.domain.member.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class MemberRepositoryTest {
  @Autowired
  private MemberRepository memberRepository;

  @Test
  void findMemberDetail() {
    log.info(memberRepository.findMemberDetail(1L));
  }
}