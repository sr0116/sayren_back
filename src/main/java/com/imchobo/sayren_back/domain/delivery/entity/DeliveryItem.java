package com.imchobo.sayren_back.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_delivery_item")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DeliveryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long deliveryItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_id", nullable = false)
  private Delivery delivery; //  다대일(N:1) 매핑

  @Column(nullable = false)
  private Long orderItemId; //  주문아이템 ID (FK)
}
