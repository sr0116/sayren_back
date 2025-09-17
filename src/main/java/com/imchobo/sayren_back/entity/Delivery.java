package com.imchobo.sayren_back.entity;

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
    private Long deliveryId;                   // PK

    @Column(nullable = false, length = 20)
    private String type;                       // DELIVERY / RETURN

    @Column(nullable = false)
    private Long memberId;                     // FK -> tbl_member.member_id (숫자만 스냅샷)

    @Column(nullable = false)
    private Long addrId;                       // FK -> tbl_address.addr_id (숫자만 스냅샷)

    @Column(name = "shipper_code", length = 50)
    private String shipperCode;                // 택배사 코드

    @Column(name = "tracking_no", length = 100, unique = false)
    private String trackingNo;                 // 송장번호 (UQ(shipper_code,tracking_no)는 DB 인덱스로 구성 권장)

    @Column(nullable = false, length = 20)
    private String status;                     // READY / PREPARING / SHIPPING / DELIVERED / PICKUP_READY / PICKED_UP

    // Delivery(1) - DeliveryItem(N)
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeliveryItem> items = new ArrayList<>();

    // 편의 메서드: item 연결/해제
    public void addItem(DeliveryItem item) {
        items.add(item);
        item.setDelivery(this);
    }
    public void removeItem(DeliveryItem item) {
        items.remove(item);
        item.setDelivery(null);
    }
}
