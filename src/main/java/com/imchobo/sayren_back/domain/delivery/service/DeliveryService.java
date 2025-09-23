package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryResponseDTO;

import java.util.List;

public interface DeliveryService {
    DeliveryResponseDTO getDelivery(Long id);
    List<DeliveryResponseDTO> getDeliveriesByMember(Long memberId);
}
