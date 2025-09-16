package com.imchobo.sayren_back.domain.subscribe_payment.entity;


import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.subscribe_payment.en.SubscribePaymentType;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
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
  // 구독 결제 pk
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscribe_payment_id")
  private Long id;

// 구독 테이블 (fk 관계)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscribe_id", nullable = false)
  private Subscribe subscribe;

  // 결제 FK (성공 시 매핑, 예정 상태일 땐 null)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  private Payment payment;

// 회차 번호
  @Column(name = "round_no", nullable = false)
  private Integer roundNo;

  // 구독 결제 유형 (MONTHLY / DEPOSIT)
  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private SubscribePaymentType type;

  // 결제 금액
  @Column(nullable = false)
  private Long amount;

  // 결제 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus payStatus;

  // 납부 예정일(스케줄링 처리에 필요)
  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  // 실제 결제 완료일
  @Column(name = "paid_date")
  private LocalDateTime paidDate;

  // 구독 결제 신청 생성일은 createEntity 사용(regDate)


  @PrePersist
  public void onCreate() {
    if(this.payStatus == null) {
      this.payStatus = PaymentStatus.PENDING;
    }
  }
}
