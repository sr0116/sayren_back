package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.*;

//  주소 등록 시 클라이언트(화면) → 서버로 들어오는 요청 데이터
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressCreateRequest {

  //  주소를 등록할 회원 번호 (tbl_member.member_id)
  private Long memberId;

  // 수령인 이름 (누가 받을지)
  private String name;

  //  수령인 연락처 (배송 기사님이 연락할 번호)
  private String tel;

  //  우편번호 (도로명 주소 검색 시 필수 값)
  private String zipcode;

  //  실제 주소 (도로명 + 상세 주소까지)
  private String address;

  //  기본 배송지 여부 (true 면 기존 기본 배송지 해제 후 이걸 기본으로 설정)
  private Boolean defaultAddress;

  //  배송 메모 (문 앞에 두세요, 경비실에 맡겨주세요 등)
  private String memo;
}
