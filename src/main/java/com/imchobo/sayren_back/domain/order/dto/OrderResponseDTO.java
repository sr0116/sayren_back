package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

  private Long orderId;           // 주문 PK
  private String status;          // 주문 상태

  private String memberEmail;     // 회원 이메일
  private String memberName;      // 회원 이름

  // 배송지 정보
  private Long addressId;
  private String addressName;
  private String addressTel;
  private String addressDetail;

  // 주문 아이템들
  private List<OrderItemResponseDTO> orderItems;

  // 시간
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}