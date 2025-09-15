package com.imchobo.sayren_back.domain.exentity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_order_plan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPlan {

  /// //요금제 (임시)///////
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "plan_id")
  private Long planId;

  @Column(nullable = false)
  private String type; // PURCHASE / RENTAL 두개 상태

  private String name;
  private Integer month;

}
