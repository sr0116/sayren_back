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

  @JsonProperty("imp_uid")
  private String impUid;

  @JsonProperty("merchant_uid")
  private String merchantUid;

  private Long amount;
  private String status;

  @JsonProperty("pay_method")
  private String payMethod;

  @JsonProperty("buyer_name")
  private String buyerName;

  @JsonProperty("buyer_email")
  private String buyerEmail;

  @JsonProperty("error_code")
  private String errorCode;

  @JsonProperty("error_msg")
  private String errorMsg;
}
