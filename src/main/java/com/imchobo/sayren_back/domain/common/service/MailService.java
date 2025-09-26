package com.imchobo.sayren_back.domain.common.service;

import com.imchobo.sayren_back.domain.common.util.MailUtil;
import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {
  private final MailUtil mailUtil;
  private final RedisUtil redisUtil;


  public void emailVerification(String email) {
    String token = UUID.randomUUID().toString();

    redisUtil.emailVerification(token, email);

    String verificationUrl = "http://localhost:8080/api/auth/email-verify/" + token;

    String title = "귀하의 세이렌 계정 이메일 주소를 확인해 주십시오.";
    String content = buildEmail(
      "세이렌 계정 이메일 인증",
      "아래 버튼을 클릭하여 이메일 주소를 인증해 주세요.",
      "이메일인증",
      "이메일 인증하기",
      verificationUrl
    );


    mailUtil.sendMail(email, title, content);
  }


  public void passwordResetEmail(String email, Long memberId) {
    String token = UUID.randomUUID().toString();

    redisUtil.setResetPassword(token, memberId);

    String verificationUrl = "http://localhost:8080/api/user/member/reset-pw/" + token;

    String title = "귀하의 세이렌 계정 비밀번호를 변경해 주십시오.";
    String content = buildEmail(
      "세이렌 계정 비밀번호 재설정",
      "아래 버튼을 클릭하여 비밀번호를 재설정해 주세요. 링크는 15분간 유효합니다.",
      "비밀번호 재설정",
      "비밀번호 변경하기",
      verificationUrl
    );

    mailUtil.sendMail(email, title, content);
  }



  private String buildEmail(String title, String message, String badgeText, String buttonText, String buttonUrl) {
    return "<body style=\"margin:0; padding:0; background-color:#f9f9f9; font-family:Arial, sans-serif;\">"
      + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
      + "style=\"background-color:#f9f9f9; padding:20px 0; max-width:400px; margin:0 auto;\">"

      // 로고 영역
      + "<tr><td style=\"padding:24px 0;\">"
      + buildLogo()
      + "</td></tr>"

      // 카드 영역
      + "<tr><td align=\"center\">"
      + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
      + "style=\"background-color:#ffffff; border-radius:12px; border:1px solid #e0e0e0; overflow:hidden;\">"

      // 뱃지
      + "<tr><td style=\"padding:20px; text-align:left;\">"
      + buildBadge(badgeText)
      + "</td></tr>"

      // 본문
      + "<tr><td style=\"padding:0 20px 20px; text-align:center;\">"
      + "<h2 style=\"margin:0 0 20px; font-size:18px; color:#000000;\">" + title + "</h2>"
      + "<p style=\"margin:0 0 30px; font-size:14px; color:#333333; line-height:1.6;\">" + message + "</p>"
      + buildButton(buttonText, buttonUrl)
      + "</td></tr>"

      // 푸터
      + "<tr><td style=\"padding:20px; text-align:center; background-color:#f9f9f9; border-top:1px solid #e0e0e0;\">"
      + buildFooter()
      + "</td></tr>"

      + "</table></td></tr></table></body>";
  }

  private String buildLogo() {
    return "<a href=\"http://localhost:3000\" class=\"logo\" style=\"display:block;\">"
      + "<img src=\"https://happygivers-bucket.s3.ap-northeast-2.amazonaws.com/sayren/sayren.png\"\n" +
      "       alt=\"SAYREN\" width=\"125\" height=\"30\"\n" +
      "       class=\"logo-light\" style=\"display:block; border:0; width:125px; height:30px; margin:0 auto; \"/>"
      + "</a>";
  }

  private String buildBadge(String text) {
    return "<span style=\"display:inline-block; padding:6px 12px; border-radius:6px;"
      + " background-color:#E93A5E; color:#ffffff; font-size:12px; font-weight:bold;\">"
      + text
      + "</span>";
  }

  private String buildButton(String text, String url) {
    return "<a href=\"" + url + "\" "
      + "style=\"display:inline-block; width:100%; max-width:360px; text-align:center;"
      + " padding:12px 0; background-color:#000000; color:#ffffff; text-decoration:none;"
      + " border-radius:8px; font-size:14px; font-weight:bold;\">"
      + text
      + "</a>";
  }

  private String buildFooter() {
    return "<p style=\"margin:0; font-size:12px; color:#888888;\">© 2025 SAYREN. All rights reserved.</p>";
  }

}
