package com.imchobo.sayren_back.domain.payment.refund.entity;

import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_refund")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund extends CreatedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "refund_id")
  private Long id;

  // NOT NULL, FK → tbl_payment.payment_id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  // 어떤 환불 요청으로부터 발생했는지 (1:1) - 추가
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "refund_request_id", nullable = false)
  private RefundRequest refundRequest;

  // 환불 금액
  // NOT NULL
  @Column(nullable = false)
  private Long amount;

  // 환불 사유 코드
  // NOT NULL, enum
  @Enumerated(EnumType.STRING)
  @Column(name = "reason_code", nullable = false)
  private ReasonCode reasonCode;

  // regDate는 CreatedEntity에서 자동 세팅
}