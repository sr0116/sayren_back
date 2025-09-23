package com.imchobo.sayren_back.domain.address.entity;

import com.imchobo.sayren_back.domain.order.entity.OrderItem;
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
    private Long Id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;


    // 주문아이템
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
}
