package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.common.entity.CreatedEntity;
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
  // 결제 상태 변경 로그 기록 테이블

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_history_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status; // 결제 상태 (PENDING/PAID/FAILED/REFUNDED)

  @Column(length = 50)
  private String reasonCode; // 공통 코드

  @Column(length = 255)
  private String reasonMessage;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ActorType actorType;

  private Long actorId;

  // 변경일시 regdate 상속

}
