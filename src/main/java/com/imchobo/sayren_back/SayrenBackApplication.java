package com.imchobo.sayren_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaAuditing
public class SayrenBackApplication {
  public static void main(String[] args) {
    SpringApplication.run(SayrenBackApplication.class, args);
  }
}
