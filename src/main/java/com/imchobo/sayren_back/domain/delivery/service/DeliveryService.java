package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;

public interface DeliveryService {
  Long createDelivery(DeliveryDTO dto);
  DeliveryDTO getDelivery(Long id);
  void updateStatus(Long id, String status, String trackingNo);
}
