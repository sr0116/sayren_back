package com.imchobo.sayren_back.domain.subscribe.entity;
import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_subscribe")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscribe extends BaseEntity {

  // 구독 PK
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  @Column(name = "subscribe_id")
  private Long id;

  // 주문 아이템 FK (필수)
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  // 구독자 회원 FK (필수):
  // NOT NULL
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 구독 상태
  // NOT NULL
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private SubscribeStatus status=SubscribeStatus.PENDING_PAYMENT;

  // 월 렌탈료 스냅샷
  // NOT NULL, 기본값 0
  @Column(name = "monthly_fee_snapshot", nullable = false)
  private Long monthlyFeeSnapshot;

  // 보증금 스냅샷
  // NOT NULL, 기본값 0
  @Column(name = "deposit_snapshot", nullable = false)
  private Long depositSnapshot;

  // 구독 시작일
  // 배송 후에 시작이라 not null 허용
  @Column(name = "start_date")
  private LocalDate startDate;

  // 구독 종료일
  // 배송 후에 계산이라 not null 허용
  @Column(name = "end_date")
  private LocalDate endDate;

  // 생성일(regDate), 수정일(modDate)은 BaseEntity 상속
}
