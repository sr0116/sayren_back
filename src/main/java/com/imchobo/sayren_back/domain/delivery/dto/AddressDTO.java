package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AddressDTO {

  private Long addrId;
  private Long memberId;
  private String name;
  private String tel;
  private String zipcode;
  private String address;
  private Boolean defaultAddress;
  private String memo;
}
