package com.imchobo.sayren_back.domain.subscribe.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.domain.exentity.Member;
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
  private Long subscribeId;

  // 나중에 이것도 조인 컬럼
  @Column(name = "order_item_id")
  private Long orderItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // enum 사용
  @Enumerated(EnumType.STRING)// 디비에 문자열로 저장
  private SubscribeStatus status;

// 월 렌탈료 스냅샷
  private  Integer monthlyFeeSnapshot;

// 보증금 스냅샷
  private Integer depositSnapshot;
// 구독 총 개월수
  private Integer totalMonths;
// 구독 시작일, 종료일
  private LocalDate startDate;
  private LocalDate endDate;
// BaseEntity에서 사용해서 따로 정의 안 해도 됨

}
