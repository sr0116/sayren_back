package com.imchobo.sayren_back.domain.order.cart.entity;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import com.imchobo.sayren_back.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_cart_item") // DB 테이블 매핑
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cart_item_id")
  private Long id;  // PK

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member; // 장바구니 소유자

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;  // 담긴 상품

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_plan_id", nullable = false)
  private OrderPlan orderPlan; // 요금제 (일반구매 시 null 가능)

  private int quantity; // 수량
}
