package com.imchobo.sayren_back.domain.payment.entity;

import com.imchobo.sayren_back.domain.common.entity.TimeRangeEntity;
import com.imchobo.sayren_back.domain.exentity.Order;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
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
  // 결제 아이디
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long id;
  // 멤버 아이디
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
  // 주문 아이디
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;
  // PortOne 고유 결제 식별자
  @Column(name = "merchant_uid", nullable = false, unique = true)
  private String merchantUid;

  //  PortOne 결제 응답
  @Column(name = "imp_uid")
  private String impUid;

  //  결제 수단
  @Column(name = "paytype")
  private String payType;

  //  총 결제 금액
  @Column(name = "amount", nullable = false)
  private Long amount;

  // 결제 상태  (PENDING / PAID / FAILED / REFUNDED)
  @Enumerated(EnumType.STRING)
  @Column(name = "paystatus", nullable = false)
  private PaymentStatus payStatus;

  private String receipt;

  // 결제 생성 시각, 취소 시각은 TimeRangeEntity
  // voidDate, regDate

}
