package com.imchobo.sayren_back.domain.delivery.address.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

  private Long id;          // PK
  private Long memberId;    // 회원 ID
  private String name;      // 수령인 이름
  private String tel;       // 연락처
  private String zipcode;   // 우편번호
  private String address;   // 주소
  private Boolean isDefault;// 기본배송지 여부
  private String memo;      // 배송 메모
//날짜 시간
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
