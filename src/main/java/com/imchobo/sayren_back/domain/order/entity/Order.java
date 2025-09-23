package com.imchobo.sayren_back.domain.order.entity;

import com.imchobo.sayren_back.domain.address.entity.Address;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.en.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import com.imchobo.sayren_back.domain.common.entity.BaseEntity;


@Entity
@Table(name = "tbl_order") // DB의 tbl_order 테이블과 매핑
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
  @Column(name = "order_id")
  private Long id;

  //  주문자 (Member)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 회원에 속함
  @JoinColumn(name = "member_id", nullable = false) // FK: tbl_member.member_id
  private Member member;  // FK → tbl_member.member_id (NOT NULL)

  //  배송지 (Address)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 주소를 가질 수 있음
  @JoinColumn(name = "address_id", nullable = false) // FK: tbl_address.addr_id
  private Address address;  // FK → tbl_address.addr_id (NOT NULL)

  // 주문 상태 (PENDING, PAID, SHIPPED, DELIVERED, CANCELED)
  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
}