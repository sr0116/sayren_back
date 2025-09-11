package com.imchobo.sayren_back.domain.payment.portone.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO + SnakeCase 전략 사용
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
  @JsonProperty("imp_key")
  private String impKey;

  @JsonProperty("imp_secret")
  private String impSecret;
}