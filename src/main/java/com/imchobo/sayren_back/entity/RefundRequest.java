package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.domain.common.entity.TimeRangeEntity;
import com.imchobo.sayren_back.en.RefundRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_subscribe_cancel")
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

  // 결제 FK (필수)
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

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
  @Column(name = "reason_code", nullable = false)
  private String reasonCode;

  // 환불 상세 사유 (회원 입력)
  // TEXT, NULL 허용
  @Column(name = "reason", columnDefinition = "TEXT")
  private String reason;

  // 처리자 (SYSTEM / ADMIN / USER)
  // NULL 허용
  @Column(name = "processed_by", length = 50)
  private String processedBy;

}
