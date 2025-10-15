package com.imchobo.sayren_back.domain.subscribe.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_subscribe")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tbl_subscribe SET deleted = true, deleted_at = NOW() WHERE subscribe_id = ?")
@Where(clause = "deleted = false")
public class Subscribe extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscribe_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private SubscribeStatus status = SubscribeStatus.PENDING_PAYMENT;

  @Column(name = "monthly_fee_snapshot", nullable = false)
  private Long monthlyFeeSnapshot;

  @Column(name = "deposit_snapshot", nullable = false)
  private Long depositSnapshot;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  // 논리 삭제 관리
  @Column(nullable = false)
  @Builder.Default
  private boolean deleted = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
