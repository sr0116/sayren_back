package com.imchobo.sayren_back.domain.common.service;

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
    mailService.emailVerification("jiyu16413@gmail.com");
  }
}