package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.common.entity.TimeRangeEntity;
import com.imchobo.sayren_back.en.PaymentType;
import com.imchobo.sayren_back.en.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends TimeRangeEntity {

  // 기본 키
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long id;

  // 결제자 회원 (FK: tbl_member.member_id)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // 주문 (FK: tbl_order.order_id)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscribe_round_id", unique = true)
  private SubscribeRound subscribeRound;

  // PortOne 고유 결제 식별자 (Not Null, Unique)
  @Column(nullable = false, length = 100, unique = true)
  private String merchantUid;

  // PortOne 결제 응답 ID (Nullable)
  @Column(length = 100)
  private String impUid;

  // 결제 수단 (TOSS, KAKAO 등) (Nullable)
  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  // 총 결제 금액 (Not Null)
  @Column(nullable = false)
  private Long amount;

  // 결제 상태 (Not Null, Enum 매핑)
  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false, length = 20)
  @Builder.Default
  private PaymentStatus paymentStatus = PaymentStatus.PENDING;

  // 영수증 URL (Nullable)
  private String receipt;

  // 생성 시각(regDate), 취소 시각(voidDate)은 TimeRangeEntity 상속
}
