package com.imchobo.sayren_back.domain.order.entity;

import com.imchobo.sayren_back.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_order_item") // DB 테이블 매핑
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_item_id") // PK 컬럼명 지정
  private Long id; // 주문아이템 PK (NOT NULL, AUTO_INCREMENT(DB에서 자동 증가))

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order; // FK(어떤 테이블 참조하는지) → tbl_order.order_id (NOT NULL)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product; // FK → tbl_product.product_id (NOT NULL)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_plan_id", nullable = false)
  private OrderPlan orderPlan;

  @Column(nullable = false)
  private Long productPriceSnapshot; // 주문 시점 상품 가격 (NOT NULL)
//   주문 시점 상품 가격 (스냅샷)
}