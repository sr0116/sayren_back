package com.imchobo.sayren_back.domain.common.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class MailUtil {

  @Value("${spring.mail.username}")
  private String from;
  private final JavaMailSender mailSender;


  @Async
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


}