package com.imchobo.sayren_back.domain.order.entity;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.delivery.entity.Address;
import jakarta.persistence.*;
import lombok.*;

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
  private Long id;

  //  주문자 (Member)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 회원에 속함
  @JoinColumn(name = "member_id", nullable = false) // FK: tbl_member.member_id
  private Member member;

  //  배송지 (Address)
  @ManyToOne(fetch = FetchType.LAZY) // 여러 주문이 하나의 주소를 가질 수 있음
  @JoinColumn(name = "addr_id", nullable = false) // FK: tbl_address.addr_id
  private Address address;

  // 주문 상태 (PENDING, PAID, SHIPPED, DELIVERED, CANCELED)
  @Column(nullable = false, length = 20)
  private String status;

  // 주문 아이템과의 1:N 관계
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItem> orderItems = new ArrayList<>();
}