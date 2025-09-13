package com.imchobo.sayren_back.domain.delivery.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.mapper.DeliveryMapper;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final DeliveryMapper deliveryMapper;

  @Override
  public Long createDelivery(DeliveryDTO dto) {
    // DTO → Entity 변환
    Delivery delivery = deliveryMapper.toEntity(dto);

    // 배송아이템 수동 매핑
    for (Long orderItemId : dto.getOrderItemIds()) {
      DeliveryItem item = DeliveryItem.builder()
        .delivery(delivery)
        .orderItemId(orderItemId)
        .build();
      delivery.getItems().add(item);
    }

    delivery.setStatus("READY");

    return deliveryRepository.save(delivery).getDeliveryId();
  }

  @Override
  public DeliveryDTO getDelivery(Long id) {
    Delivery delivery = deliveryRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("배송을 찾을 수 없습니다."));

    DeliveryDTO dto = deliveryMapper.toDTO(delivery);
    dto.setOrderItemIds(
      delivery.getItems().stream().map(DeliveryItem::getOrderItemId).toList()
    );

    return dto;
  }

  @Override
  public void updateStatus(Long id, String status, String trackingNo) {
    Delivery delivery = deliveryRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("배송을 찾을 수 없습니다."));

    delivery.setStatus(status);
    if (trackingNo != null) {
      delivery.setTrackingNo(trackingNo);
    }
  }
}
