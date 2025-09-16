package com.imchobo.sayren_back.domain.payment.portone.dto.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenBody {
  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expired_at")
  private Long expiredAt;

  @JsonProperty("now")
  private Long now;
}
