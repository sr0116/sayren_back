package com.imchobo.sayren_back.domain.subscribe.service;

import com.imchobo.sayren_back.domain.address.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.address.entity.Delivery;
import com.imchobo.sayren_back.domain.address.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.exception.SubscribeNotFoundException;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExDeliveryServie {
  private final DeliveryRepository deliveryRepository;
  private final SubscribeRepository subscribeRepository;
  private final DeliveryItemRepository deliveryItemRepository;


//  @Transactional
//  public void updateStatus(Long deliveryItemId, DeliveryStatus newStatus) {
//    DeliveryItem deliveryItem = deliveryItemRepository.findById(deliveryItemId)
//            .orElseThrow(() -> new RuntimeException("deliveryItem not found"));
//
//    deliveryItem.setStatus(newStatus);
//
//    // 배송 완료 → 구독 활성화
//    if (newStatus == DeliveryStatus.DELIVERED) {
//      OrderItem orderItem = deliveryItem.getOrderItem();
//
//      Subscribe subscribe = subscribeRepository.findByOrderItem(orderItem)
//              .orElseThrow(() -> new SubscribeNotFoundException(orderItem.getId()));
//
//      subscribe.setStatus(SubscribeStatus.ACTIVE);
//      subscribeRepository.save(subscribe);
//    }
//  }
}
