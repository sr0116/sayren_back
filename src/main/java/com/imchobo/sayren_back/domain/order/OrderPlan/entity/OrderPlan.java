package com.imchobo.sayren_back.domain.order.OrderPlan.entity;

import com.imchobo.sayren_back.domain.order.en.OrderPlanType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_order_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_plan_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderPlanType type;

  private Integer month;
}
