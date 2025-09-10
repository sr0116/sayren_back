package com.imchobo.sayren_back.service;

import com.imchobo.sayren_back.domain.delivery.dto.DeliveryDTO;
import com.imchobo.sayren_back.domain.delivery.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DeliveryServiceTest {

  @Autowired
  private DeliveryService deliveryServices;
  @Autowired
  private DeliveryService deliveryService;

  @Test
  void testCreateDelivery() {
    // DeliveryDTO 생성 (더미 값 포함)
    DeliveryDTO dto = DeliveryDTO.builder()
      .type("DELIVERY")
      .memberId(1L)           // tbl_member.member_id = 1
      .addrId(1L)             //  방금 INSERT한 주소
      .status("READY")
      .trackingNo("TEMP-123")
      .orderItemIds(List.of(1L)) // ⚠️ tbl_order_item에도 데이터가 있어야 함
      .build();

    // 저장 실행
    Long deliveryId = deliveryService.createDelivery(dto);

    // 검증
    assertThat(deliveryId).isNotNull();
    System.out.println(" 배송 생성 성공, deliveryId = " + deliveryId);
  }
}
