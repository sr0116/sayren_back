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
    mailService.emailVerification("manlubo11@gmail.com");
  }
}