package com.imchobo.sayren_back.domain.payment.portone.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TokenResponse {

  private Integer code;
  private String message;
  private TokenBody response;
}
