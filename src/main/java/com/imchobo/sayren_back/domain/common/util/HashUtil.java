package com.imchobo.sayren_back.domain.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class HashUtil {
  @Value("${jwt.secret}")
  private String secret;

  public String encode(String input) {
    return DigestUtils.sha256Hex(input +  secret);
  }
}
