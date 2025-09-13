package com.imchobo.sayren_back.domain.delivery.entity;

import com.imchobo.sayren_back.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_delivery")  // DB 테이블 이름
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long deliveryId;

  @Column(nullable = false)
  private String type;  // DELIVERY / RETURN

  @Column(nullable = false)
  private Long memberId; // 주문자 ID (FK → tbl_member)

  @Column(nullable = false)
  private Long addrId;   // 배송지 ID (FK → tbl_address)

  private String shipperCode;  // 택배사 코드
  private String trackingNo;   // 송장번호
  private String status;       // READY / PREPARING / SHIPPING / DELIVERED

  // 배송 ↔ 배송아이템 (1:N 관계)
  @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<DeliveryItem> items = new ArrayList<>();
}
