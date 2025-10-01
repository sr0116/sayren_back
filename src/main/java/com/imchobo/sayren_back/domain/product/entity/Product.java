package com.imchobo.sayren_back.domain.product.entity;

import com.imchobo.sayren_back.domain.common.entity.CreatedEntity;
import com.imchobo.sayren_back.domain.order.OrderPlan.entity.OrderPlan;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "tbl_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Product extends CreatedEntity {

  // 상품번호 (PK)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long id;

  // 상품명
  @Column(nullable = false)
  private String name;

  // 판매가, 정상가
  @Column(nullable = false)
  private Long price;

  // 상품 상태
  @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
  private Boolean isUse = false;

  // 상품 설명(상세설명 포함)
  @Lob // (mediumtext로 되어있기때문에 Lob이 긴 텍스트 저장 컬럼)
  @Column(nullable = false)
  private String description;

  // 상품 카테고리
  @Column(nullable = false)
  private String productCategory;

  // 상품 모델명(시리얼 넘버. 리스트 페이지랑 연결됨)
  @Column(nullable = false, unique = true)
  private String modelName;

  @ManyToMany
  @JoinTable(
          name = "product_order_plan", // 중간 테이블
          joinColumns = @JoinColumn(name = "product_id"), // 현재 엔티티(Product) FK
          inverseJoinColumns = @JoinColumn(name = "order_plan_id") // 반대쪽 엔티티(OrderPlan) FK
  )
  private List<OrderPlan> orderPlans;

}
