package com.imchobo.sayren_back.domain.delivery.orchestration;

public interface DeliveryCreationService {
  //결제후 배송 레코드 생성
  void createIfAbsent(Long orderId);
}
