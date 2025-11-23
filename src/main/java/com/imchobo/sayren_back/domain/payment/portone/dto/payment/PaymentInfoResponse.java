package com.imchobo.sayren_back.domain.payment.portone.dto.payment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.imchobo.sayren_back.domain.payment.en.PaymentType;
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

  @JsonProperty("pg_provider")
  private String pgProvider;
  private PaymentType paymentType; // 위의 pg_provider 변환

  @JsonProperty("pay_method")
  private String payMethod;

  private String status;

  @JsonProperty("fail_reason")
  private String failReason;   // 사용자 취소, 잔액 부족 등 이유

  @JsonProperty("error_code")
  private String errorCode;  // PortOne 내부 에러 코드

  @JsonProperty("receipt_url")
  private String receiptUrl;


}