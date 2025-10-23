package com.imchobo.sayren_back.domain.common.util;

import com.imchobo.sayren_back.domain.member.recode.LatestTerms;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class RedisUtilTest {

  @Autowired
  RedisUtil redisUtil;

  @Test
  void set() {
    redisUtil.set("test60", "test60", 60, TimeUnit.SECONDS);
  }

  @Test
  void testSet() {
    redisUtil.set("test", "test");
  }

  @Test
  void get() {
    log.info(redisUtil.get("EMAIL_VERIFY:token"));
  }

  @Test
  void delete() {
    redisUtil.delete("test");
  }

  @Test
  void hasKey() {
    log.info(redisUtil.hasKey("test"));
    log.info(redisUtil.hasKey("test60"));
  }

  @Test
  void emailVerification() {
    redisUtil.emailVerification("token", "user@gmail.com");
  }


}