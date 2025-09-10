package com.imchobo.sayren_back.domain.member.mapper;

import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class MemberMapperTest {
  @Autowired
  private MemberMapper memberMapper;

  @Test
  void toEntity() {
    String email = "user@gmail.com";
    String password = "password";
    String name = "유저";
    boolean serviceAgree = true;
    boolean privacyAgree = true;
    MemberSignupDTO dto = new MemberSignupDTO(email, password, name, serviceAgree, privacyAgree);

    log.info(dto);
    log.info(memberMapper.toEntity(dto));
  }

  @Test
  void toAuthDTO() {
  }
}