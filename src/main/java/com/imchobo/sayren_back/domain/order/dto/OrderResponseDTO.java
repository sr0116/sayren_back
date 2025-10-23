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

  private Long orderId;        // 주문 PK
  private String status;       // 주문 상태

  // 회원 정보
  private String memberEmail;  // 주문자 이메일
  private String memberName;   // 주문자 이름

  // 배송지 정보
  private Long addressId;      // 배송지 PK
  private String addressName;  // 수령인 이름
  private String addressTel;   // 수령인 연락처
  private String addressZipcode; // 우편번호
  private String addressDetail;  // 상세주소
  private String addressMemo;    // 배송 메모

  // 주문 아이템들
  private List<OrderItemResponseDTO> orderItems;
//주문이력
  private List<OrderHistoryResponseDTO> histories;

  // 등록/수정 시간
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}