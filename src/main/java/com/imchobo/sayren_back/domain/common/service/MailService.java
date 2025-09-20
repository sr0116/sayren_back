package com.imchobo.sayren_back.domain.common.service;

import com.imchobo.sayren_back.domain.common.util.MailUtil;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {
  private final MailUtil mailUtil;
  private final RedisUtil redisUtil;


  public void emailVerification(String email){
    String token = UUID.randomUUID().toString();

    redisUtil.emailVerification(token, email);

    String verificationUrl = "http://localhost:8080/api/auth/email-verify/" + token;

    String title = "귀하의 세이렌 계정 이메일 주소를 확인해 주십시오.";
    String content =
      "<h2>세이렌 계정 이메일 인증</h2>"
        + "<p>아래 버튼을 클릭하여 이메일 주소를 인증해 주세요.</p>"
        + "<a href=\"" + verificationUrl + "\" "
        + "style=\"display:inline-block;padding:10px 20px;"
        + "background-color:#4CAF50;color:#fff;text-decoration:none;"
        + "border-radius:5px;\">이메일 인증하기</a>";

    mailUtil.sendMail(email, title, content);
  }

}
