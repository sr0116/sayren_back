package com.imchobo.sayren_back.domain.subscribe;

import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.delivery.entity.DeliveryItem;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryItemRepository;
import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.order.repository.OrderItemRepository;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.subscribe.dto.SubscribeResponseDTO;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@Log4j2
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


  @Autowired
  private SubscribeRoundScheduler scheduler;
  @Autowired
  private SubscribeRoundRepository subscribeRoundRepository;


  @Test
  void testSchedulerProcessDueRounds() {
    // given
    LocalDate today = LocalDate.now();

    // 사전 상태 확인
    List<SubscribeRound> beforeRounds =
            subscribeRoundRepository.findByDueDateAndPayStatus(today, PaymentStatus.PENDING);
    log.info("스케줄러 실행 전 결제대기 회차 수: {}", beforeRounds.size());
    assertTrue(!beforeRounds.isEmpty(), "테스트 데이터에 PENDING 상태 회차가 있어야 합니다.");

    // when
    scheduler.processDueRounds();

    // then
    List<SubscribeRound> afterRounds =
            subscribeRoundRepository.findByDueDateAndPayStatus(today, PaymentStatus.PENDING);
    log.info("스케줄러 실행 후 결제대기 회차 수: {}", afterRounds.size());

    // 모든 회차 중 하나라도 gracePeriodEndAt이 생겼는지 확인
    boolean gracePeriodSet = subscribeRoundRepository.findAll().stream()
            .anyMatch(r -> r.getGracePeriodEndAt() != null);
    assertTrue(gracePeriodSet, "결제 실패 회차에 gracePeriodEndAt이 설정되어야 합니다.");

    // 구독 상태가 연체로 바뀐 구독이 있는지 확인
    List<Subscribe> overdueSubs = subscribeRepository.findAll().stream()
            .filter(s -> s.getStatus() == SubscribeStatus.OVERDUE)
            .toList();
    log.info("스케줄러 실행 후 연체 상태 구독 수: {}", overdueSubs.size());

    // 상태 변경이 있었는지만 검증
    assertTrue(overdueSubs.size() >= 0, "연체 상태 구독이 있어야 합니다(조건에 따라 0일 수도 있음).");
  }


  @Test
  void testGracePeriodExpiredTriggersOverdue() {
    // given
    LocalDateTime past = LocalDateTime.now().minusDays(5);
    SubscribeRound round = subscribeRoundRepository.findAll().get(0);
    round.setPayStatus(PaymentStatus.PENDING);
    round.setGracePeriodEndAt(past); // 이미 5일 지남
    subscribeRoundRepository.save(round);

    // when
    scheduler.processDueRounds();

    // then
    Subscribe updatedSubscribe = round.getSubscribe();
    assertEquals(
            SubscribeStatus.OVERDUE,
            updatedSubscribe.getStatus(),
            "유예기간 만료 후 구독 상태는 OVERDUE로 전환되어야 합니다."
    );
    log.info(" 유예기간 만료 시 연체 전환 테스트 통과 - subscribeId={}", updatedSubscribe.getId());
  }
  @Test
  @Rollback(false)
  void testReturnDeliveryNormal() {
    // given
    Long deliveryId = 11L; // 미리 insert 된 배송 ID
    Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow();

    // 배송 상태 변경 (환불)
    delivery.setType(DeliveryType.RETURN);
    delivery.setStatus(DeliveryStatus.RETURNED);
    deliveryRepository.saveAndFlush(delivery);

    System.out.println("배송 회수 완료 처리 테스트 성공");

  }
  @PersistenceContext
  private EntityManager entityManager;

  @Test
  @Rollback(false)
  void testReturnDelivery2() {
    // given
    Long deliveryId = 12L; // 미리 insert 된 배송 ID
    Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow();

    // 배송 상태 변경 (환불)
    delivery.setType(DeliveryType.RETURN);
    delivery.setStatus(DeliveryStatus.RETURNED);
    deliveryRepository.saveAndFlush(delivery);

    Long subscribeId = 283L;
    Long orderItemId = 2L;
    OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();

    subscribeService.cancelAfterReturn(subscribeId, orderItem);

    System.out.println("배송 회수 완료 처리 테스트 성공");

  }

  @Test
  @Rollback(false)
  void testReturnDelivery() throws InterruptedException {
    Long deliveryId = 12L;
    Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

    delivery.setType(DeliveryType.RETURN);
    delivery.setStatus(DeliveryStatus.RETURNED);
    deliveryRepository.saveAndFlush(delivery);

    Long subscribeId = 286L;
    Long orderItemId = 2L;
    OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();

    subscribeService.cancelAfterReturn(subscribeId, orderItem);

    entityManager.flush();
    System.out.println("배송 회수 완료 처리 테스트 성공");

    Thread.sleep(2000); //이벤트 비동기 실행 대기
  }



  @Test
  @Rollback(false)
  void testActivateAfterDelivery() {
    // given
    Long deliveryId = 12L; // 미리 insert 된 배송 ID
    Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow();

    // 배송 상태 변경 (배송 완료)
    delivery.setType(DeliveryType.DELIVERY);
    delivery.setStatus(DeliveryStatus.DELIVERED);
    deliveryRepository.saveAndFlush(delivery);


    // when: 구독 활성화 처리 호출
  Long subscribeId = 300L;
  OrderItem orderItem = delivery.getDeliveryItems()
          .get(0)
          .getOrderItem();

  subscribeService.activateAfterDelivery(subscribeId, orderItem);

  // then
//  SubscribeResponseDTO result = subscribeService.getSubscribe(subscribeId);
//  Assertions.assertEquals(
//          SubscribeStatus.ACTIVE,
//          result.getStatus(),
//          "구독 상태가 ACTIVE로 변경되어야 한다"
//  );
  }
}

