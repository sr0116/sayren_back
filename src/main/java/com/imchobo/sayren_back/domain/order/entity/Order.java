package com.imchobo.sayren_back.domain.order.entity;

import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import com.imchobo.sayren_back.domain.common.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tbl_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long id;

  //  주문자 (Member)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 회원에 속함
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  //  배송지 (Address)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 주소를 가질 수 있음
  @JoinColumn(name = "address_id", nullable = false)
  private Address address;

  // 주문 상태 (PENDING, PAID, SHIPPED, DELIVERED, CANCELED)
  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

      //주문 아이템 연관관계 추가
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems;
    //히스토리 주문이력
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderHistory> histories = new ArrayList<>();


}