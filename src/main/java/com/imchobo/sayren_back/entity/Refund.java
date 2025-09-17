package com.imchobo.sayren_back.domain.payment.refund.entity;


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
  // 환불 내역만 처리(환불 처리만 담당)

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "refund_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @Column(nullable = false)
  private Long amount; // 환불 금액

  @Column(nullable = false, length = 50)
  private String reasonCode; // 환불 사유 코드 (Enum 기반)
  private String reason; // 환불 사유
  // 환불일시 regdate는 상속

}
