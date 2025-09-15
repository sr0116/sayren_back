package com.imchobo.sayren_back.domain.subscribe.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntityEx;
import com.imchobo.sayren_back.domain.exentity.MemberEx;
import com.imchobo.sayren_back.domain.exentity.OrderItem;
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
public class Subscribe extends BaseEntityEx {

  // 구독 아이디
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  @Column(name = "subscribe_id")
  private Long subscribeId;

  // 나중에 이것도 조인 컬럼
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEx memberEx;

  // enum 사용
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)// 디비에 문자열로 저장
  private SubscribeStatus status;

// 월 렌탈료 스냅샷
  private  Integer monthlyFeeSnapshot;

// 보증금 스냅샷
  private Integer depositSnapshot;
// 구독 총 개월수
  private Integer totalMonths;
// 구독 시작일, 종료일
// BaseEntity에서 사용해서 따로 정의 안 해도 됨

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;
}
