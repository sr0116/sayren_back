package com.imchobo.sayren_back.domain.subscribe;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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
  @Autowired
  private DeliveryRepository deliveryRepository;


  @Test
  @Transactional
  @Rollback(false)
  void testActivateAfterDelivery() {
    // given
    Long deliveryId = 1L; // 미리 insert 된 배송 ID
    Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow();

    // 배송 상태 변경
    delivery.setStatus(DeliveryStatus.DELIVERED);
    deliveryRepository.save(delivery);

    // when: 구독 활성화 처리 호출
    Long subscribeId = 198L;
    OrderItem orderItem = delivery.getDeliveryItems()
            .get(0)
            .getOrderItem();

    subscribeService.activateAfterDelivery(subscribeId, orderItem);

    // then
    SubscribeResponseDTO result = subscribeService.getSubscribe(subscribeId);
    Assertions.assertEquals(
            SubscribeStatus.ACTIVE,
            result.getStatus(),
            "구독 상태가 ACTIVE로 변경되어야 한다"
    );
  }
}

