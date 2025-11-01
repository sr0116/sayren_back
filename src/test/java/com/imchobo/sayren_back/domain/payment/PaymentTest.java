package com.imchobo.sayren_back.domain.payment;

import com.imchobo.sayren_back.domain.common.en.ActorType;
import com.imchobo.sayren_back.domain.delivery.component.DeliveryStatusChanger;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import com.imchobo.sayren_back.domain.subscribe.repository.SubscribeRepository;
import com.imchobo.sayren_back.domain.subscribe.service.SubscribeService;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.service.SubscribeRoundScheduler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Log4j2
public class PaymentTest {



  @PersistenceContext
  private EntityManager entityManager;
  @Autowired
  private DeliveryRepository deliveryRepository;
  @Autowired
  private DeliveryStatusChanger deliveryStatusChanger;

  @Test
  @Rollback(false)
  void testReturnDelivery() throws InterruptedException {
    // 테스트할 배송 ID
    Long deliveryId = 11L;

    // 배송 조회
    Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new RuntimeException("배송 정보 없음: " + deliveryId));

    // 관련된 첫 번째 주문 아이템 가져오기
    if (delivery.getDeliveryItems().isEmpty()) {
      throw new RuntimeException("배송에 연결된 주문 아이템이 없습니다.");
    }

    OrderItem orderItem = delivery.getDeliveryItems().get(0).getOrderItem();

    // 배송 상태 변경 (회수 완료)
    deliveryStatusChanger.changeDeliveryStatus(
            delivery,
            DeliveryType.RETURN,
            DeliveryStatus.RETURNED,
            orderItem
    );

    System.out.println("배송 회수 완료 처리 테스트 성공");

    // 이벤트 비동기 처리 대기 (2초 정도)
    Thread.sleep(2000);
  }

  @Test
  @Rollback(false)
  void testDelivery() throws InterruptedException {
    // 테스트할 배송 ID
    Long deliveryId = 11L;

    // 배송 조회
    Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new RuntimeException("배송 정보 없음: " + deliveryId));

    // 관련된 첫 번째 주문 아이템 가져오기
    if (delivery.getDeliveryItems().isEmpty()) {
      throw new RuntimeException("배송에 연결된 주문 아이템이 없습니다.");
    }

    OrderItem orderItem = delivery.getDeliveryItems().get(0).getOrderItem();

    // 배송 상태 변경 (회수 완료)
    deliveryStatusChanger.changeDeliveryStatus(
            delivery,
            DeliveryType.DELIVERY,
            DeliveryStatus.DELIVERED,
            orderItem
    );

    System.out.println("배송 완료 처리 테스트 성공");

    // 이벤트 비동기 처리 대기 (2초 정도)
    Thread.sleep(2000);
  }


}

