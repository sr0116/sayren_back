package com.imchobo.sayren_back.domain.common.service;

import com.imchobo.sayren_back.domain.member.dto.EmailVerifyRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MailServiceTest {
  @Autowired
  private MailService mailService;

  @Test
  void emailVerification() {
    mailService.emailVerification(new EmailVerifyRequestDTO("jiyu16413@gmail.com"));
  }
}