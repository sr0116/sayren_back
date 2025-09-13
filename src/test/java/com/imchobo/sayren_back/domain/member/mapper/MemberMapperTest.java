package com.imchobo.sayren_back.domain.member.mapper;

import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("회원가입 DTO > 엔티티 매핑")
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
  @DisplayName("엔티티 > 인증 DTO 매핑")
  void toAuthDTO() {
    String email = "user@gmail.com";
    String password = "password";
    String name = "유저";

    Member member = Member.builder().email(email).password(password).name(name).status(MemberStatus.ACTIVE).build();

    MemberAuthDTO memberAuthDTO = memberMapper.toAuthDTO(member);
    log.info(memberAuthDTO);
  }
}