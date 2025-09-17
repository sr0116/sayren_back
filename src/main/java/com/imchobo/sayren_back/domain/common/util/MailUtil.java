package com.imchobo.sayren_back.domain.common.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MailUtil {

  @Value("${spring.mail.username}")
  private String from;
  private final JavaMailSender mailSender;
  private final RedisUtil redisUtil;

  public void sendMail(String email, String title, String content) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

      helper.setTo(email);
      helper.setSubject(title);
      helper.setText(content, true);
      helper.setFrom(from, "Sayren Team");

      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      throw new RuntimeException("메일 전송 실패", e);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public void emailVerification(String email){
    String token = UUID.randomUUID().toString();

    redisUtil.emailVerification(token, email);

    String verificationUrl = "http://localhost:8080/api/auth/email-verify?token=" + token;

    String title = "귀하의 세이렌 계정 이메일 주소를 확인해 주십시오.";
    String content =
            "<h2>세이렌 계정 이메일 인증</h2>"
            + "<p>아래 버튼을 클릭하여 이메일 주소를 인증해 주세요.</p>"
            + "<a href=\"" + verificationUrl + "\" "
            + "style=\"display:inline-block;padding:10px 20px;"
            + "background-color:#4CAF50;color:#fff;text-decoration:none;"
            + "border-radius:5px;\">이메일 인증하기</a>";

    sendMail(email, title, content);
  }
}