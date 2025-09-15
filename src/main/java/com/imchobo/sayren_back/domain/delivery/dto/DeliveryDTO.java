package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DeliveryDTO {

    private Long deliveryId;        // 배송 PK
    private String type;            // DELIVERY / RETURN
    private Long memberId;          // 회원 ID
    private Long addrId;            // 주소 ID
    private String shipperCode;     // 택배사 코드
    private String trackingNo;      // 송장번호
    private String status;          // READY / PREPARING / SHIPPING / DELIVERED / PICKUP_READY / PICKED_UP
    private List<Long> orderItemIds;// 배송에 포함된 주문아이템 ID 목록
}
