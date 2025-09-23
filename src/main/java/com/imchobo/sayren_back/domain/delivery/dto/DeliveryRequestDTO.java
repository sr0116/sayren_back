package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryRequestDTO {
    private Long orderId;   // 주문 ID (필수)
    private String address; // 배송지
}
