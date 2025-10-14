package com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_subscribe_round")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeRound extends CreatedEntity {

  // 구독 결제 PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscribe_round_id")
  private Long id;

  // 구독 FK (필수)
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "subscribe_id", nullable = false)
  private Subscribe subscribe;

  // 회차 번호 (1, 2, 3 …)
  // NOT NULL
  @Column(nullable = false)
  private int roundNo;

  // 결제 금액
  // NOT NULL
  @Column(nullable = false)
  private Long amount;

  // 결제 상태 (PENDING / PAID / FAILED / REFUNDED)
  // Enum, VARCHAR(20), NOT NULL
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private PaymentStatus payStatus = PaymentStatus.PENDING;

  // 납부 예정일 (스케줄링에 필요)
  // NOT NULL
  private LocalDate dueDate;

  // 실제 결제 완료일 (NULL 허용)
  @Column(name = "paid_date")
  private LocalDateTime paidDate;
///  스케줄링에 필요한 컬컴 추가
  // 결제 실패 시각
  @Column(name = "failed_at")
  private LocalDateTime failedAt;

  // 유예기간 종료일 (예: failedAt + 3일)
  @Column(name = "grace_period_end_at")
  private LocalDateTime gracePeriodEndAt;


  // 생성일(regDate)은 CreatedEntity에서 자동 관리
}
