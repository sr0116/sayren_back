package com.imchobo.sayren_back.domain.subscribe_payment.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.exentity.OrderPlan;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.en.SubscribePaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_subscribe_payment")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscribePayment extends CreatedEntity {

  // 구독 결제 PK
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscribe_payment_id")
  private Long id;

  // 구독 FK (필수)
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "subscribe_id", nullable = false)
  private Subscribe subscribe;

  // 요금제(plan) FK (필수)
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "plan_id", nullable = false)
  private OrderPlan plan;

  // 결제 FK (성공 시 매핑, 예정 상태일 땐 NULL 허용)
  // NULL 허용
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  // 회차 번호 (1, 2, 3 …)
  // NOT NULL
  @Column(name = "round_no", nullable = false)
  private Integer roundNo;

  // 구독 결제 유형 (MONTHLY / DEPOSIT)
  // Enum,  NOT NULL
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private SubscribePaymentType type;

  // 결제 금액
  // NOT NULL
  @Column(nullable = false)
  private Long amount;

  // 결제 상태 (PENDING / PAID / FAILED / REFUNDED)
  // Enum, VARCHAR(20), NOT NULL
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus payStatus;

  // 납부 예정일 (스케줄링에 필요)
  // NOT NULL
  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  // 실제 결제 완료일 (NULL 허용)
  @Column(name = "paid_date")
  private LocalDateTime paidDate;

  // 생성일(regDate)은 CreatedEntity에서 자동 관리
}
