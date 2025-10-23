package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryRequestDTO {
    private Long addressId;   // 배송지 ID
    private String type;      // DELIVERY / RETURN
}
