package com.imchobo.sayren_back.domain.order.entity;


import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_order_history") // DB 테이블 매핑
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistory extends CreatedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_history_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(nullable = false)
  private OrderStatus status; // 변경시점 상태

//  @Column(nullable = false)
//  private String address; // 변경시점 배송지

    @Column(name = "address", nullable = false)
    private String address; // 변경시점 배송지


    @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ActorType changedBy; // 변경 주체
}
