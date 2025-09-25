package com.imchobo.sayren_back.domain.delivery.service.factory;

import com.imchobo.sayren_back.domain.delivery.address.entity.Address;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryStatus;
import com.imchobo.sayren_back.domain.delivery.en.DeliveryType;
import com.imchobo.sayren_back.domain.delivery.entity.Delivery;
import com.imchobo.sayren_back.domain.member.entity.Member;
import org.springframework.stereotype.Component;

/**
 * Delivery 엔티티 생성 책임 전담
 */
@Component
public class DeliveryFactory {

  public Delivery create(Member member, Address address) {
    return Delivery.builder()
      .member(member)
      .address(address)
      .type(DeliveryType.DELIVERY)
      .status(DeliveryStatus.READY)
      .build();
  }
}
