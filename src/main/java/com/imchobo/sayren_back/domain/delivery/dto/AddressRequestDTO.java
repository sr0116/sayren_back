package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {
  private String name;       // 수령인 이름
  private String tel;        // 연락처
  private String zipcode;    // 우편번호
  private String address;    // 주소 (도로명+상세)
  private Boolean isDefault; // 기본배송지 여부
  private String memo;       // 배송 메모
}
