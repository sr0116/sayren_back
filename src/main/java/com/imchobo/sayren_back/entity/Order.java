package com.imchobo.sayren_back.entity;

import jakarta.persistence.*;
import lombok.*;
import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import java.util.ArrayList;
import java.util.List;

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
  @Column(name = "order_id") // PK 컬럼명 지정
  private Long id; // 주문 PK (NOT NULL, AUTO_INCREMENT)

  //  주문자 (Member)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 회원에 속함
  @JoinColumn(name = "member_id", nullable = false) // FK: tbl_member.member_id
  private Member member;  // FK → tbl_member.member_id (NOT NULL)

  //  배송지 (Address)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 주소를 가질 수 있음
  @JoinColumn(name = "addr_id", nullable = false) // FK: tbl_address.addr_id
  private Address address;  // FK → tbl_address.addr_id (NOT NULL)

  // 주문 상태 (PENDING, PAID, SHIPPED, DELIVERED, CANCELED)
  @Column(nullable = false, length = 20)
  private String status; // 주문 상태 (PENDING, PAID, SHIPPED, DELIVERED, CANCELED) (NOT NULL, 최대 20자)

  // 주문 아이템과의 1:N 관계
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItem> orderItems = new ArrayList<>(); // 주문-주문아이템 매핑 (1:N)
}