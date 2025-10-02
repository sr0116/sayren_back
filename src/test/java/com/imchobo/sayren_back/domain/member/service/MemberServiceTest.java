package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.MemberSignupDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
class MemberServiceTest {
  @Autowired
  private MemberService memberService;


  @Test
  @DisplayName("회원가입 테스트")
  void register() {
<<<<<<< HEAD
//    String email = "user1@gmail.com";
//    String password = "password";
//    String name = "유저1";
//    boolean serviceAgree = true;
//    boolean privacyAgree = true;
//    MemberSignupDTO dto = new MemberSignupDTO(email, password, name, serviceAgree, privacyAgree);
//    memberService.register(dto);
//    log.info(memberService.findByEmail(email));
=======
    String email = "user1@gmail.com";
    String password = "password";
    String name = "유저1";
    boolean serviceAgree = true;
    boolean privacyAgree = true;
    MemberSignupDTO dto = new MemberSignupDTO(email, password, name, serviceAgree, privacyAgree, "");
    memberService.register(dto);
    log.info(memberService.findByEmail(email));
>>>>>>> origin/feature/member
  }


  @Test
  @DisplayName("핸드폰 번호 수정 테스트")
  void modifyTel() {
    
  }

  @Test
  @DisplayName("권한 부여 or 제거")
  void changeRole(){
    memberService.changeRole(17L);
  }

  @Test
  @DisplayName("멤버 상세정보가져오기")
  void getMemberInfo() {
    memberService.getMemberInfo(1L);
  }
}