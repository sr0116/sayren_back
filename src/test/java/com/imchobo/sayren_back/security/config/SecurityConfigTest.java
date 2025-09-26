package com.imchobo.sayren_back.security.config;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Log4j2
class SecurityConfigTest {
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void passwordEncoder() {
    passwordEncoder.encode("123456");
    log.info(passwordEncoder.matches("123456",passwordEncoder.encode("123456")));
  }
}