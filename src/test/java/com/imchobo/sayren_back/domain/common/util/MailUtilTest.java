package com.imchobo.sayren_back.domain.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MailUtilTest {
  @Autowired
  MailUtil mailUtil;


  @Test
  void sendMail() {
    mailUtil.sendMail("manlubo11@gmail.com", "메일 테스트", "내용입니다.");
  }

  @Test
  void emailVerification() {
    mailUtil.emailVerification("manlubo11@gmail.com");
  }
}