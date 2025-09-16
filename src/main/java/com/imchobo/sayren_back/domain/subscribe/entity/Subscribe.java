package com.imchobo.sayren_back.domain.subscribe.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.exentity.OrderItem;
import com.imchobo.sayren_back.domain.member.entity.Member;
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

  // 구독 아이디
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  @Column(name = "subscribe_id")
  private Long id;

  // 주문 아이템
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  //구독 상태
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SubscribeStatus status;

// 월 렌탈료 스냅샷
@Column(name = "monthly_fee_snapshot", nullable = false)
  private  Integer monthlyFeeSnapshot;

// 보증금 스냅샷
@Column(name = "deposit_snapshot", nullable = false)
  private Integer depositSnapshot;
// 구독 총 개월수
@Column(name = "total_months")
  private Integer totalMonths;

// BaseEntity에서 사용해서 따로 정의 안 해도 됨 (구독 기간)

// 구독 시작일, 종료일
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;
}
