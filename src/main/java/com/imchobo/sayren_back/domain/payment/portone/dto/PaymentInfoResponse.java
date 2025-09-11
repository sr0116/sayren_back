package com.imchobo.sayren_back.domain.payment.portone.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentInfoResponse {
  // 정보 dto
  @JsonProperty("imp_uid")
  private String impUid;
  @JsonProperty("merchant_uid")
  private String merchantUid;
  private Long amount;
  private String status;

}
