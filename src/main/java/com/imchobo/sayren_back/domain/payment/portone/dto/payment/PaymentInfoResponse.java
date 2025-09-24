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
public class PaymentInfoResponse {


  @JsonProperty("imp_uid")
  private String impUid;

  @JsonProperty("merchant_uid")
  private String merchantUid;

  private Long amount;
  private String status;

  @JsonProperty("fail_reason")
  private String failReason;   // 사용자 취소, 잔액 부족 등 이유

  @JsonProperty("error_code")
  private String errorCode;  // PortOne 내부 에러 코드


}