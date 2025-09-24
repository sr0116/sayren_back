package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeliveryRequestDTO {
    private Long memberId;   // 필수
    private Long addressId;  // 필수
    private String type;     // DELIVERY / RETURN
}
