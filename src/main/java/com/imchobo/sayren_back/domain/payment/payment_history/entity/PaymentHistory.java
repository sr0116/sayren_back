package com.imchobo.sayren_back.domain.payment.payment_history.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
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

  @Column(nullable = false)
  private String status;

  private String reasonCode;    // 오류 코드나 사유 코드
  private String reasonMessage; // 상세 메시지

  private String actorType;     // 변경 주체 타입 (SYSTEM/USER/ADMIN)
  private Long actorId;

  // 변경일시 regdate 상속

}
