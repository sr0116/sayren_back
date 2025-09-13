package com.imchobo.sayren_back.domain.security.util;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.domain.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class JwtUtilTest {
  @Autowired
  private JwtUtil jwtUtil;
  @Autowired
  private MemberMapper memberMapper;


  @Test
  @DisplayName("Access Token 생성 및 검증")
  void accessToken() {
    Member member = Member.builder().email("user@gmail.com").tel("010-2345-2345").name("김유저").build();


    // when
    String token = jwtUtil.generateAccessToken(memberMapper.toAuthDTO(member));
    Claims claims = jwtUtil.validateToken(token);

    log.info(token);
    log.info(claims);
  }

  @Test
  @DisplayName("Refresh Token 생성 및 검증")
  void refreshToken() {
    MemberAuthDTO member = MemberAuthDTO.builder().email("user@gmail.com").build();
    // when
    String token = jwtUtil.generateRefreshToken(member);
    Claims claims = jwtUtil.validateToken(token);

    log.info(token);
    log.info(claims);
  }
}
