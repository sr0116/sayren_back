package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MemberTermServiceImplTest {
  @Autowired
  private MemberTermService memberTermService;

  @Test
  void saveTerm() {
    Member member = Member.builder().id(1L).build();
    memberTermService.saveTerm(member);
  }
}