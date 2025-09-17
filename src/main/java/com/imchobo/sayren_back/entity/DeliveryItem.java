package com.imchobo.sayren_back.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_delivery_item")      // 스키마: tbl_delivery_item
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_item_id")
    private Long deliveryItemId;    // PK (NOT NULL(null이되면안됨), AUTO_INCREMENT(DB에서 자동 증가))

    // N:1 - 배송 헤더
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;   // FK → tbl_delivery.delivery_id (NOT NULL)

    // 주문아이템 ID만 매핑(숫자 스냅샷)
    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;    // FK → tbl_order_item.order_item_id (NOT NULL)
}
