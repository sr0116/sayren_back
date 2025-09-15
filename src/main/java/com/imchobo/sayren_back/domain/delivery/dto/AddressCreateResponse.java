package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.*;
import java.time.LocalDateTime;

// 주소 등록 후 서버 → 클라이언트로 내려주는 응답 데이터
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressCreateResponse {

  //  새로 생성된 주소의 PK (tbl_address.addr_id)
  private Long addrId;

  //  수령인 이름 (등록된 값 그대로 반환)
  private String name;

  //  수령인 연락처
  private String tel;

  //  우편번호
  private String zipcode;

  //  주소 (도로명 + 상세)
  private String address;

  //  기본 배송지 여부
  private Boolean defaultAddress;

  //  배송 메모
  private String memo;

  //  등록된 시간 (BaseEntity.regDate)
  private LocalDateTime regDate;

  //  수정된 시간 (BaseEntity.modDate) — 처음 등록 시 null 일 수도 있음
  private LocalDateTime modDate;
}
