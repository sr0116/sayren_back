package com.imchobo.sayren_back.domain.subscribe;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SubscribeTest {
  @Autowired
  private SubscribeService subscribeService;

  @Autowired
  private SubscribeRepository subscribeRepository;

  @Autowired
  private OrderItemRepository orderItemRepository;

  @Autowired
  private DeliveryItemRepository deliveryItemRepository;

  @Test
  void Test() {
    // given
//    OrderItem orderItem = orderItemRepository.save();
//    Subscribe subscribe = subscribeService.createSubscribe(dto, orderItem);
//
//    Delivery delivery = new Delivery();
//    delivery.setStatus(DeliveryStatus.DELIVERED);
//    deliveryItemRepository.save(
//            DeliveryItem.builder()
//                    .delivery(delivery)
//                    .orderItem(orderItem)
//                    .build()
//    );
//
//    // when
//    subscribeService.activateAfterDelivery(subscribe.getId(), orderItem);
//
//    // then
//    Subscribe updated = subscribeRepository.findById(subscribe.getId()).orElseThrow();
//    assertEquals(SubscribeStatus.ACTIVE, updated.getStatus());
  }
}

