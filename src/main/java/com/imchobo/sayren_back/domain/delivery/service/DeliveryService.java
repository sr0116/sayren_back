package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;

public interface DeliveryService {
    Long createDelivery(DeliveryDTO dto);                       // 배송 생성(출고/회수 공통)
    DeliveryDTO getDelivery(Long id);                           // 단건 조회
    void updateStatus(Long id, String status, String trackingNo); // 상태 변경(+송장번호)
}
