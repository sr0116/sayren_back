package com.imchobo.sayren_back.domain.payment.portone.dto.payment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentVerifyResponse {
  // 포트원 결제 검증 응답 DTO
  @JsonProperty("imp_uid")
  private String impUid;

  @JsonProperty("merchant_uid")
  private String merchantUid;

  private Long amount;
  private String status;
  private String payMethod;
  private String buyerName;
  private String buyerEmail;
}
