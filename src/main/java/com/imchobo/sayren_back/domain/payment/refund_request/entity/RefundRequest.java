package com.imchobo.sayren_back.domain.payment.refund_request.entity;

import com.imchobo.sayren_back.common.entity.TimeRangeEntity;
import com.imchobo.sayren_back.domain.exentity.OrderItem;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.payment.refund_request.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.subscribe.en.ReasonCode;
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
  @JoinColumn(name = "order_item_id", nullable = false)
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
