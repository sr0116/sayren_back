package com.imchobo.sayren_back.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import com.imchobo.sayren_back.en.DeliveryStatus;
import com.imchobo.sayren_back.en.DeliveryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_delivery")                // 스키마: tbl_delivery
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "delivery_id")
    private Long id;  // PK (NOT NULL, AUTO_INCREMENT)

    // 배송 타입 (DELIVERY / RETURN)
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DeliveryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    // 배송 상태 (READY / PREPARING / SHIPPING / DELIVERED / PICKUP_READY / PICKED_UP) (NOT NULL, 최대 20자)
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
