package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.domain.common.entity.TimeRangeEntity;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RefundRequestStatus status; // REQUESTED / APPROVED / REJECTED / CANCELED

  @Column(nullable = false, length = 50)
  private String reasonCode; // 공통 사유 코드 (Enum으로 관리)

  @Column(columnDefinition = "TEXT")
  private String reason; // 상세 사유 (회원 입력)

  private String processedBy; // 처리자 (SYSTEM/ADMIN/USER)

}
