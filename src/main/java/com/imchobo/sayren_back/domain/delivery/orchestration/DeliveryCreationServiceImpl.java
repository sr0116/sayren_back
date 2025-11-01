package com.imchobo.sayren_back.domain.delivery.orchestration;

import com.imchobo.sayren_back.domain.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class DeliveryCreationServiceImpl implements DeliveryCreationService {

//  private final OrderRepository orderRepository;
  private final DeliveryRepository deliveryRepository;

  @Override
  public void createIfAbsent(Long orderId) {
//    // 1) 주문 조회
//    Order order = orderRepository.findById(orderId)
//      .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId=" + orderId));
//
//    // 2) 주소 확인
//    Address address = order.getAddress();
//    if (address == null) {
//      throw new IllegalStateException("주문에 배송지(address)가 없습니다. orderId=" + orderId);
//    }
//
//    // 3) 배송 생성 (현재 모델: Delivery에 Order FK 없음)
//    Delivery delivery = Delivery.builder()
//      .member(order.getMember())       //  order에서 member 가져와 세팅
//      .address(address)                //  order의 address 사용
//      .type(DeliveryType.DELIVERY)
//      .status(DeliveryStatus.READY)
//      .build();
//
//    deliveryRepository.save(delivery);
//    log.info("[DeliveryCreation] CREATED deliveryId={} for orderId={}", delivery.getId(), orderId);
  }
}
