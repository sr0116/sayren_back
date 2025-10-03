package com.imchobo.sayren_back.domain.order.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

  private Long addressId;    // 기존 배송지 ID (선택)
  private String receiverName;   // 새 배송지일 경우 수령인
  private String receiverTel;    // 새 배송지일 경우 연락처
  private String zipcode;        // 새 배송지일 경우 우편번호
  private String detail;         // 새 배송지일 경우 상세주소
  private String memo;           // 배송 메모

}
