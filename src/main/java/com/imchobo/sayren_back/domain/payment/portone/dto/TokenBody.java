package com.imchobo.sayren_back.domain.payment.portone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenBody {
  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expire_at")
  private Long expireAt;

}
