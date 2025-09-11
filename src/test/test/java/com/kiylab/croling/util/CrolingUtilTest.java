package com.kiylab.croling.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CrolingUtilTest {
  @Autowired
  private CrolingUtil crolingUtil;


  @Test
  public void crolingTest() throws Exception {
    crolingUtil.printPageHtml("https://www.lge.co.kr/category/refrigerators");

  }
}
