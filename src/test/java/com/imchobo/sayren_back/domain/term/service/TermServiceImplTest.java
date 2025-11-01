package com.imchobo.sayren_back.domain.term.service;

import com.imchobo.sayren_back.domain.term.en.TermType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TermServiceImplTest {
  @Autowired
  TermService termService;

  @Test
  void revalidateTerm() {
    termService.revalidateTerm(TermType.SERVICE);
    termService.revalidateTerm(TermType.PRIVACY);
  }
}