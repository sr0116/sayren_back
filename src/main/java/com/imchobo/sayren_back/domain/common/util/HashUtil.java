package com.imchobo.sayren_back.domain.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class HashUtil {

  private final String secret;

  public HashUtil(@Value("${jwt.secret:}") String secretFromValue, Environment env) {
    if (secretFromValue == null || secretFromValue.isBlank()) {
      // fallback: 혹시라도 spring.config.additional-location에서 못 불러오면
      secretFromValue = env.getProperty("jwt.secret", "security-jwt-study-key-this-is-key");
    }
    this.secret = secretFromValue;
  }

  public String encode(String input) {
    return DigestUtils.sha256Hex(input + secret);
  }
}
