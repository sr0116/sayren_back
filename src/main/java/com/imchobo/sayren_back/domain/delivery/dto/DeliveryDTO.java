package com.imchobo.sayren_back.domain.delivery.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DeliveryDTO {

  private Long deliveryId;
  private String type;
  private Long memberId;
  private Long addrId;
  private String shipperCode;
  private String trackingNo;
  private String status;
  private List<Long> orderItemIds; //  배송에 속한 주문아이템 ID 목록
}
