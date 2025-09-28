package com.imchobo.sayren_back.domain.payment.refund.entity;

import com.imchobo.sayren_back.domain.common.entity.TimeRangeEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_refund_request")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest extends TimeRangeEntity {
  // 환불 요청 (고객 요청 -> 관리자 처리 관계)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "refund_request_id")
  private Long id;

  // 주문 아이템
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id") // 자동 환불 고려하려면 nullable 허용
  private OrderItem orderItem;

  // 환불 요청 회원 FK (필수)
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 환불 요청 상태 (REQUESTED / APPROVED / REJECTED / CANCELED)
  // NOT NULL
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RefundRequestStatus status;

  // 환불 사유 코드 (Enum 관리)
  //  NOT NULL
  @Enumerated(EnumType.STRING)
  @Column(name = "reason_code", nullable = false)
  private ReasonCode reasonCode;
}
