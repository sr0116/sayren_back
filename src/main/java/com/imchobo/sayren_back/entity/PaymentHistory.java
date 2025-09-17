package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.en.ActorType;
import com.imchobo.sayren_back.en.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_payment_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory extends CreatedEntity {

  // 결제 상태 변경 로그 PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_history_id")
  private Long id;

  // 결제 FK (필수)
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  // 변경된 결제 상태 (PENDING / PAID / FAILED / REFUNDED)
  // Enum,  NOT NULL
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  // 사유 코드 (예: 환불 코드, 오류 코드)
  @Column(name = "reason_code")
  private String reasonCode;

  // 상세 사유 메시지
  // VARCHAR(255), NULL 허용
  @Column(name = "reason_message", length = 255)
  private String reasonMessage;

  // 변경 주체 타입 (SYSTEM / USER / ADMIN)
  // Enum, NOT NULL
  @Enumerated(EnumType.STRING)
  @Column(name = "actor_type", nullable = false)
  private ActorType actorType;

  // 변경 주체 ID (회원/관리자 FK, NULL 허용)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;


  // 생성일시는 CreatedEntity에서 상속 (regDate 자동 기록)
}
