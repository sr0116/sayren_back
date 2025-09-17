package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
  // 주문 기본 정보
  private Long orderId;           // 주문 PK
  private String status;          // 주문 상태 (PENDING, PAID, ...)

  // 회원 정보 포함
  private Long memberId;          // 회원 ID
  private String memberEmail;     // 회원 이메일
  private String memberName;      // 회원 이름

  // 배송지 정보 포함
  private Long addressId;         // 배송지 ID
  private String addressName;     // 수령인 이름
  private String addressTel;      // 연락처
  private String addressDetail;   // 상세 주소 (addr)

  // 생성/수정 시간
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}