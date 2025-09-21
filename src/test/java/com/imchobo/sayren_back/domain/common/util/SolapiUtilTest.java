package com.imchobo.sayren_back.domain.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SolapiUtilTest {
  @Autowired
  private SolapiUtil solapiUtil;


  @Test
  void sendSms() {
    solapiUtil.sendSms("01066878628");
  }
}