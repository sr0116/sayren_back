package com.imchobo.sayren_back.domain.member.recode;

import jakarta.validation.constraints.NotEmpty;

public record AccessToken (
  @NotEmpty(message = "엑세스 토큰이 필요합니다.") String accessToken
){

}
