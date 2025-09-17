package com.imchobo.sayren_back.service;


import com.imchobo.sayren_back.domain.order.dto.OrderItemResponseDTO;
import com.imchobo.sayren_back.domain.order.service.OrderService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest // 스프링 컨텍스트를 띄워서 JPA/Hikari/빈 등록까지 전부 실제처럼 테스트
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // @Order 순서대로 테스트 실행
public class OrderServiceImplTest {

  @Autowired
  private OrderService orderService;
  // 실제 OrderServiceImpl 주입 (스프링 빈 등록된 걸 가져옴)

  private static Long savedOrderId;
  // 주문 등록 후 다른 테스트에서 참조할 ID 저장용

  /**
   * 1 주문 등록 테스트
   */
  @Test
  @Order(1) // 제일 먼저 실행
  void 주문등록() {
    // 주문 아이템 DTO 생성 (상품 1개 담았다고 가정)
    OrderItemResponseDTO item = OrderItemResponseDTO.builder()
      .productId(1L) // 테스트용 상품 ID
      .planId(null) // 일반 구매라면 NULL
      .productPriceSnapshot(10000) // 주문 시점 가격
      .build();

    // 주문 DTO 생성
    OrderDTO dto = OrderDTO.builder()
      .memberId(1L) // 회원 1번이 주문한다고 가정
      .addrId(1L)   // 배송지 1번 사용한다고 가정
      .status("PENDING") // 초기 상태는 PENDING
      .orderItems(Collections.singletonList(item)) // 아이템 1개 담기
      .build();

    // 주문 생성 실행
    OrderDTO response = orderService.createOrder(dto);

    // 검증
    assertThat(response).isNotNull(); // 응답이 Null 아니어야 함
    assertThat(response.getOrderId()).isNotNull(); // 생성된 주문 PK 확인
    assertThat(response.getStatus()).isEqualTo("PENDING"); // 상태 확인

    savedOrderId = response.getOrderId(); // 나중 테스트 위해 저장

    System.out.println(" 주문 등록 완료: 주문ID=" + savedOrderId);
  }

  /**
   * 2  주문 단건 조회 테스트
   */
  @Test
  @Order(2)
  void 주문단건조회() {
    // 1번 테스트에서 저장해둔 주문 ID로 조회
    OrderDTO dto = orderService.getOrder(savedOrderId);

    // 검증
    assertThat(dto).isNotNull(); // 조회 결과 Null 아님
    assertThat(dto.getOrderId()).isEqualTo(savedOrderId); // ID 일치 확인
    assertThat(dto.getMemberId()).isEqualTo(1L); // 회원 ID 확인
    assertThat(dto.getAddrId()).isEqualTo(1L); // 배송지 ID 확인

    System.out.println(" 단건 조회된 주문: " + dto);
  }

  /**
   * 3 회원별 주문 목록 조회 테스트
   */
  @Test
  @Order(3)
  void 회원별주문조회() {
    // 회원 ID=1 기준으로 모든 주문 조회
    List<OrderDTO> list = orderService.getOrdersByMember(1L);

    // 검증
    assertThat(list).isNotEmpty(); // 결과가 비어있으면 안 됨
    assertThat(list.size()).isGreaterThan(0); // 최소 1개 이상 있어야 함

    list.forEach(o -> System.out.println(" 조회된 주문: " + o));
  }
}
