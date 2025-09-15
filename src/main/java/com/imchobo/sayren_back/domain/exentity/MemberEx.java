package com.imchobo.sayren_back.domain.exentity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEx {
  /// ////////////   임시 //////////////////

  /** 회원 PK */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long memberId;

  /** 이메일 (unique) */
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  /** 비밀번호 (소셜로그인 계정이면 NULL 허용) */
  @Column(name = "password")
  private String password;

  /** 이름 */
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /** 휴대폰 번호 */
  @Column(name = "tel", length = 20)
  private String tel;

  /** 상태 (READY, ACTIVE, DISABLED, DELETED) */
  @Column(name = "status", nullable = false, length = 20)
  private String status;

  /** 이메일 인증 여부 */
  @Column(name = "email_verified")
  private Boolean emailVerified;

  /** 가입일 */
  @Column(name = "regdate", nullable = false)
  private LocalDateTime regdate;

  /** 수정일 */
  @Column(name = "moddate")
  private LocalDateTime moddate;

  // === JPA 라이프사이클 ===
  @PrePersist
  public void onCreate() {
    this.regdate = LocalDateTime.now();
    if (this.status == null) {
      this.status = "READY"; // 기본 상태
    }
    if (this.emailVerified == null) {
      this.emailVerified = false; // 기본값 미인증
    }
  }

  @PreUpdate
  public void onUpdate() {
    this.moddate = LocalDateTime.now();
  }
}