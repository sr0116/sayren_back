package com.imchobo.sayren_back.domain.subscribe.subscribe_cancel.entity;


import com.imchobo.sayren_back.domain.common.entity.TimeRangeEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.subscribe_cancel.en.CancelSubscribeStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_subscribe_cancel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeCancel extends TimeRangeEntity {
  // 구독 해지 요청 (고객 요청 -> 관리자 처리 관계)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cancel_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscribe_id", nullable = false)
  private Subscribe subscribe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  private String reason; // 해지 사유

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private CancelSubscribeStatus status; // 처리 상태

  private String processedBy; // 처리자


}
