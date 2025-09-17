package com.imchobo.sayren_back.domain.delivery.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_delivery")                // 스키마: tbl_delivery
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {   // regdate/moddate 포함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "delivery_id")
    private Long deliveryId;  // PK (NOT NULL, AUTO_INCREMENT)

    @Column(nullable = false, length = 20)
    private String type;      // 배송 타입 (DELIVERY / RETURN) (NOT NULL, 최대 20자)

    @Column(nullable = false)
    private Long memberId;    // FK(어떤 테이블 참조하는지) → tbl_member.member_id (NOT NULL)

    @Column(nullable = false)
    private Long addrId;    // FK → tbl_address.addr_id (NOT NULL)

    @Column(name = "shipper_code", length = 50)
    private String shipperCode;    // 택배사 코드 (NULL 허용, 최대 50자)

    @Column(name = "tracking_no", length = 100, unique = false)
    private String trackingNo;  // 송장번호 (NULL 허용, 최대 100자, DB에서 UQ(shipper_code, tracking_no)

    @Column(nullable = false, length = 20)
    private String status;   // 배송 상태 (READY / PREPARING / SHIPPING / DELIVERED / PICKUP_READY / PICKED_UP) (NOT NULL, 최대 20자)
    // Delivery(1) - DeliveryItem(N)
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeliveryItem> items = new ArrayList<>(); // 1:N, 배송-아이템 매핑

    // 편의 메서드: item 연결/해제
//    public void addItem(DeliveryItem item) {
//        items.add(item);
//        item.setDelivery(this);
//    }
//    public void removeItem(DeliveryItem item) {
//        items.remove(item);
//        item.setDelivery(null);
//    }
}
