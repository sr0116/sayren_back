package com.imchobo.sayren_back.domain.payment.portone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CancelResponse {
  //환불 dto
  @JsonProperty("imp_uid")
  private String impUid;
  @JsonProperty("merchant_uid")
  private String merchantUid;

  @JsonProperty("amount")
  private Long amount;

  private String reason;
}
