package com.imchobo.sayren_back.domain.payment.portone.dto.token;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRequest {

  @JsonProperty("imp_key")
  @NotBlank(message = "impKey는 필수입니다.")
  private String impKey;

  @JsonProperty("imp_secret")
  @NotBlank(message = "impSecret는 필수입니다.")
  private String impSecret;
}